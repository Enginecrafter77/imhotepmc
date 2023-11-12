package dev.enginecrafter77.imhotepmc.net.stream.msg;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class PacketStreamStartMessage implements IMessage {
	private UUID requestId;
	private String topic;

	public PacketStreamStartMessage()
	{
		this.requestId = null;
		this.topic = null;
	}

	public PacketStreamStartMessage(String topic, UUID requestId)
	{
		this.requestId = requestId;
		this.topic = topic;
	}

	public String getTopic()
	{
		return this.topic;
	}

	public PacketStreamStartConfirmMessage confirm(UUID channelId)
	{
		return new PacketStreamStartConfirmMessage(channelId, this.requestId);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		long rlsb = buf.readLong();
		long rmsb = buf.readLong();
		this.requestId = new UUID(rmsb, rlsb);
		this.topic = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(this.requestId.getLeastSignificantBits());
		buf.writeLong(this.requestId.getMostSignificantBits());
		ByteBufUtils.writeUTF8String(buf, this.topic);
	}
}
