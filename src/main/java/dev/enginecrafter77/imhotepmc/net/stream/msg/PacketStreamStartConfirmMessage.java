package dev.enginecrafter77.imhotepmc.net.stream.msg;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketStreamStartConfirmMessage extends PacketStreamChannelMessage {
	private UUID requestId;

	public PacketStreamStartConfirmMessage()
	{
		super();
		this.requestId = null;
	}

	public PacketStreamStartConfirmMessage(UUID channelId, UUID requestId)
	{
		super(channelId);
		this.requestId = requestId;
	}

	public UUID getRequestId()
	{
		return this.requestId;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);
		long rlsb = buf.readLong();
		long rmsb = buf.readLong();
		this.requestId = new UUID(rmsb, rlsb);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);
		buf.writeLong(this.requestId.getLeastSignificantBits());
		buf.writeLong(this.requestId.getMostSignificantBits());
	}
}
