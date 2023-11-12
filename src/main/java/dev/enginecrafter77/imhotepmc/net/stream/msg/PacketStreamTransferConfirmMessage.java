package dev.enginecrafter77.imhotepmc.net.stream.msg;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketStreamTransferConfirmMessage extends PacketStreamChannelMessage {
	private UUID transactionId;

	public PacketStreamTransferConfirmMessage()
	{
		super();
		this.transactionId = null;
	}

	public PacketStreamTransferConfirmMessage(UUID channelId, UUID transactionId)
	{
		super(channelId);
		this.transactionId = transactionId;
	}

	public UUID getTransactionId()
	{
		return this.transactionId;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);
		long tlsb = buf.readLong();
		long tmsb = buf.readLong();
		this.transactionId = new UUID(tmsb, tlsb);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);
		buf.writeLong(this.transactionId.getLeastSignificantBits());
		buf.writeLong(this.transactionId.getMostSignificantBits());
	}
}
