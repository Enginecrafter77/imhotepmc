package dev.enginecrafter77.imhotepmc.net.stream.msg;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class PacketStreamChannelMessage implements IMessage {
	private UUID channelId;

	public PacketStreamChannelMessage()
	{
		this.channelId = null;
	}

	public PacketStreamChannelMessage(UUID channelId)
	{
		this.channelId = channelId;
	}

	public UUID getChannelId()
	{
		return this.channelId;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		long lsb = buf.readLong();
		long msb = buf.readLong();
		this.channelId = new UUID(msb, lsb);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(this.channelId.getLeastSignificantBits());
		buf.writeLong(this.channelId.getMostSignificantBits());
	}
}
