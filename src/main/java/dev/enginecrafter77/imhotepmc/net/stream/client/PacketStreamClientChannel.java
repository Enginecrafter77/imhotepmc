package dev.enginecrafter77.imhotepmc.net.stream.client;

import dev.enginecrafter77.imhotepmc.net.stream.PacketStreamChunk;
import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamEndMessage;
import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamTransferMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PacketStreamClientChannel implements Closeable {
	private static final Log LOGGER = LogFactory.getLog(PacketStreamClientChannel.class);

	private static final int MAX_BUFFER_SIZE = 16384;

	private final SimpleNetworkWrapper networkWrapper;

	private final Map<UUID, PacketStreamChunk> transfers;
	private final UUID channelID;
	private final ByteBuf buffer;
	private final int bufferSize;

	private final Runnable disposeAction;

	private int transferLastOrdinal;
	private boolean closing;

	public PacketStreamClientChannel(SimpleNetworkWrapper networkWrapper, UUID channelID, int bufferSize, Runnable disposeAction)
	{
		if(bufferSize > MAX_BUFFER_SIZE)
			throw new IllegalArgumentException("Buffer size cannot be greater than packet capacity!");

		this.closing = false;
		this.disposeAction = disposeAction;
		this.transferLastOrdinal = -1;
		this.bufferSize = bufferSize;
		this.networkWrapper = networkWrapper;
		this.transfers = new TreeMap<UUID, PacketStreamChunk>();
		this.channelID = channelID;
		this.buffer = Unpooled.buffer(bufferSize, bufferSize);
	}

	public int getBufferSize()
	{
		return this.bufferSize;
	}

	public void flush()
	{
		if(this.buffer.readableBytes() == 0)
			return;

		PacketStreamChunk chunk = PacketStreamChunk.claim(this.buffer);
		this.buffer.writerIndex(0);
		this.buffer.readerIndex(0);

		UUID transactionId = UUID.randomUUID();
		this.transfers.put(transactionId, chunk);

		LOGGER.info(String.format("Channel %s: Flush %d bytes (tr: %s)", this.channelID, chunk.getLength(), transactionId));
		PacketStreamTransferMessage message = new PacketStreamTransferMessage(this.channelID, transactionId, chunk, ++this.transferLastOrdinal);
		this.networkWrapper.sendToServer(message);
	}

	public void confirmTransaction(UUID transactionId)
	{
		LOGGER.info(String.format("Channel %s: CONFIRM %s", this.channelID, transactionId));
		PacketStreamChunk chunk = this.transfers.remove(transactionId);
		if(chunk == null)
			return;
		chunk.release();
		this.checkTransactionsAndClose();
	}

	protected ByteBuf getBuffer()
	{
		return this.buffer;
	}

	@Override
	public void close()
	{
		this.closing = true;
		this.checkTransactionsAndClose();
	}

	private void checkTransactionsAndClose()
	{
		if(this.closing && this.transfers.isEmpty())
		{
			LOGGER.info(String.format("Channel %s: REQUESTING CLOSE", this.channelID));
			PacketStreamEndMessage msg = new PacketStreamEndMessage(this.channelID);
			this.networkWrapper.sendToServer(msg);
			this.buffer.release();
			this.disposeAction.run();
		}
	}

	public OutputStream getOutputStream()
	{
		return new PacketStreamOutputStream();
	}

	public class PacketStreamOutputStream extends OutputStream
	{
		private final Lock writeLock;

		public PacketStreamOutputStream()
		{
			this.writeLock = new ReentrantLock();
		}

		@Override
		public void write(@Nonnull byte[] byteArray, int off, int len)
		{
			this.writeLock.lock();
			int rem = PacketStreamClientChannel.this.getBuffer().writableBytes();
			if(len > rem)
			{
				this.write(byteArray, off, rem);
				PacketStreamClientChannel.this.flush();
				off += rem;
				len -= rem;
			}
			PacketStreamClientChannel.this.getBuffer().writeBytes(byteArray, off, len);
			this.writeLock.unlock();
		}

		@Override
		public void write(int bt)
		{
			this.writeLock.lock();
			if(PacketStreamClientChannel.this.getBuffer().writableBytes() == 0)
				PacketStreamClientChannel.this.flush();
			PacketStreamClientChannel.this.getBuffer().writeByte(bt);
			this.writeLock.lock();
		}

		@Override
		public void flush()
		{
			this.writeLock.lock();
			PacketStreamClientChannel.this.flush();
			this.writeLock.lock();
		}

		@Override
		public void close()
		{
			this.writeLock.lock();
			this.flush();
			PacketStreamClientChannel.this.close();
			this.writeLock.unlock();
		}
	}
}
