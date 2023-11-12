package dev.enginecrafter77.imhotepmc.net.stream.server;

import dev.enginecrafter77.imhotepmc.net.stream.PacketStreamChunk;
import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamEndMessage;
import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamStartMessage;
import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamTransferMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class PacketStreamManager {
	private static final Log LOGGER = LogFactory.getLog(PacketStreamManager.class);

	private final Map<UUID, PacketStreamServerChannel> transactionMap;
	private final Map<String, PacketStreamTopicHandler> handlers;

	public PacketStreamManager()
	{
		this.transactionMap = new TreeMap<UUID, PacketStreamServerChannel>();
		this.handlers = new HashMap<String, PacketStreamTopicHandler>();
	}

	public void subscribe(String topic, PacketStreamTopicHandler handler)
	{
		this.handlers.put(topic, handler);
	}

	public UUID openChannel(MessageContext ctx, String topic)
	{
		PacketStreamTopicHandler handler = this.handlers.get(topic);
		UUID channelId = UUID.randomUUID();
		PacketStreamServerChannel channel = handler.openChannel(ctx);
		this.transactionMap.put(channelId, channel);
		return channelId;
	}

	public void closeChannel(MessageContext ctx, UUID channelId)
	{
		PacketStreamServerChannel channel = this.transactionMap.remove(channelId);
		if(channel == null)
		{
			LOGGER.error("Unable to close non existent channel");
			return;
		}
		channel.close(ctx);
	}

	public PacketStreamServerChannel getChannel(UUID channel)
	{
		return this.transactionMap.get(channel);
	}

	public IMessageHandler<PacketStreamStartMessage, IMessage> getStartHandler()
	{
		return this::onStartMessageReceived;
	}

	public IMessageHandler<PacketStreamTransferMessage, IMessage> getTransferHandler()
	{
		return this::onTransferMessageReceived;
	}

	public IMessageHandler<PacketStreamEndMessage, IMessage> getEndHandler()
	{
		return this::onEndMessageReceived;
	}

	public void register(SimpleNetworkWrapper wrapper, int ds, int dt, int de)
	{
		wrapper.registerMessage(this.getStartHandler(), PacketStreamStartMessage.class, ds, Side.SERVER);
		wrapper.registerMessage(this.getTransferHandler(), PacketStreamTransferMessage.class, dt, Side.SERVER);
		wrapper.registerMessage(this.getEndHandler(), PacketStreamEndMessage.class, de, Side.SERVER);
	}

	protected IMessage onStartMessageReceived(PacketStreamStartMessage message, MessageContext ctx)
	{
		UUID channelId = this.openChannel(ctx, message.getTopic());
		LOGGER.info(String.format("Channel %s OPEN (topic:%s)", channelId, message.getTopic()));
		return message.confirm(channelId);
	}

	protected IMessage onTransferMessageReceived(PacketStreamTransferMessage message, MessageContext ctx)
	{
		PacketStreamServerChannel channel = this.getChannel(message.getChannelId());

		LOGGER.info(String.format("Transfer %s chunk #%d (%s:%dB)", message.getChannelId(), message.getOrdinal(), message.getTransactionId(), message.getChunk().getLength()));

		PacketStreamChunk chunk = message.getChunk();
		channel.acceptData(ctx, chunk);
		chunk.release();

		return message.confirmMessage();
	}

	protected IMessage onEndMessageReceived(PacketStreamEndMessage message, MessageContext ctx)
	{
		LOGGER.info(String.format("Channel %s CLOSED", message.getChannelId()));
		this.closeChannel(ctx, message.getChannelId());
		return null;
	}
}
