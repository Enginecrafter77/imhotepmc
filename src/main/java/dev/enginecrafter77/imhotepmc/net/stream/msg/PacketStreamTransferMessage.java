package dev.enginecrafter77.imhotepmc.net.stream.msg;

import dev.enginecrafter77.imhotepmc.net.stream.PacketStreamChunk;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketStreamTransferMessage extends PacketStreamChannelMessage {
	private UUID transactionId;
	private PacketStreamChunk chunk;
	private int ordinal;

	public PacketStreamTransferMessage()
	{
		super();
		this.transactionId = null;
		this.chunk = null;
		this.ordinal = 0;
	}

	public PacketStreamTransferMessage(UUID channel, UUID transactionId, PacketStreamChunk chunk, int ordinal)
	{
		super(channel);
		this.transactionId = transactionId;
		this.chunk = chunk;
		this.ordinal = ordinal;
	}

	public PacketStreamChunk getChunk()
	{
		return this.chunk;
	}

	public int getOrdinal()
	{
		return this.ordinal;
	}

	public UUID getTransactionId()
	{
		return this.transactionId;
	}

	public PacketStreamTransferConfirmMessage confirmMessage()
	{
		return new PacketStreamTransferConfirmMessage(this.getChannelId(), this.transactionId);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);
		long tlsb = buf.readLong();
		long tmsb = buf.readLong();
		this.transactionId = new UUID(tmsb, tlsb);
		this.ordinal = buf.readInt();
		this.chunk = PacketStreamChunk.read(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);
		buf.writeLong(this.transactionId.getLeastSignificantBits());
		buf.writeLong(this.transactionId.getMostSignificantBits());
		buf.writeInt(this.ordinal);
		this.chunk.writeTo(buf);
	}
}
