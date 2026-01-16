package dev.enginecrafter77.imhotepmc.net.stream.client;

import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamStartConfirmMessage;
import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamStartMessage;
import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamTransferConfirmMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class PacketStreamDispatcher {
	private static final Logger LOGGER = LogManager.getLogger(PacketStreamDispatcher.class);

	private final Map<UUID, PacketStreamClientChannel> channels;
	private final Map<UUID, ClientChannelAction> requests;

	private final SimpleNetworkWrapper networkWrapper;

	private final int bufferSize;

	public PacketStreamDispatcher(SimpleNetworkWrapper networkWrapper, int bufferSize)
	{
		this.channels = new TreeMap<UUID, PacketStreamClientChannel>();
		this.requests = new TreeMap<UUID, ClientChannelAction>();
		this.networkWrapper = networkWrapper;
		this.bufferSize = bufferSize;
	}

	public void connect(String topic, ClientChannelAction action)
	{
		UUID reqID = UUID.randomUUID();
		this.requests.put(reqID, action);

		PacketStreamStartMessage msg = new PacketStreamStartMessage(topic, reqID);
		this.networkWrapper.sendToServer(msg);
	}

	public IMessageHandler<PacketStreamStartConfirmMessage, IMessage> getStartConfirmHandler()
	{
		return this::onStartConfirmMessage;
	}

	public IMessageHandler<PacketStreamTransferConfirmMessage, IMessage> getTransferConfimHandler()
	{
		return this::onTransferConfirmMessage;
	}

	public IMessage onStartConfirmMessage(PacketStreamStartConfirmMessage message, MessageContext ctx)
	{
		ClientChannelAction action = this.requests.remove(message.getRequestId());
		if(action == null)
		{
			LOGGER.error("Invalid packet stream start message received (request not found)");
			return null;
		}

		UUID channelId = message.getChannelId();
		PacketStreamClientChannel channel = new PacketStreamClientChannel(this.networkWrapper, channelId, this.bufferSize, () -> {
			//noinspection resource
			this.channels.remove(channelId);
		});
		this.channels.put(message.getChannelId(), channel);
		action.onClientChannelObtained(channel);
		return null;
	}

	public IMessage onTransferConfirmMessage(PacketStreamTransferConfirmMessage message, MessageContext ctx)
	{
		PacketStreamClientChannel channel = this.channels.get(message.getChannelId());
		if(channel == null)
		{
			LOGGER.error("Invalid packet transfer message confirm received (channel does not exist)");
			return null;
		}
		channel.confirmTransaction(message.getTransactionId());
		return null;
	}
}
