package dev.enginecrafter77.imhotepmc.net.stream;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.OutputStream;

public class PacketStreamChunk {
	private final ByteBuf buffer;
	private final int length;

	public PacketStreamChunk(ByteBuf buffer, int length)
	{
		this.buffer = buffer;
		this.length = length;
	}

	public ByteBuf getBuffer()
	{
		return this.buffer;
	}

	public int getLength()
	{
		return this.length;
	}

	public void release()
	{
		this.buffer.release();
	}

	public void writeTo(ByteBuf buf)
	{
		buf.writeInt(this.length);
		buf.writeBytes(this.buffer, this.length);
	}

	public void transferTo(OutputStream output) throws IOException
	{
		this.buffer.readBytes(output, this.length);
	}

	public static PacketStreamChunk claim(ByteBuf buffer)
	{
		ByteBuf slice = buffer.copy();
		int length = slice.readableBytes();
		return new PacketStreamChunk(slice, length);
	}

	public static PacketStreamChunk read(ByteBuf buffer)
	{
		int length = buffer.readInt();
		return new PacketStreamChunk(buffer.readRetainedSlice(length), length);
	}
}
