package dev.enginecrafter77.imhotepmc.util;

import java.util.Iterator;

public class ArrayIterable<T> implements Iterable<T> {
	private final T[] src;
	private final int offset;
	private final int length;

	public ArrayIterable(T[] src, int offset, int length)
	{
		this.src = src;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public Iterator<T> iterator()
	{
		return new ArrayIterator<T>(this.src, this.offset, this.length);
	}

	public static <T> ArrayIterable<T> wrap(T[] src)
	{
		return new ArrayIterable<T>(src, 0, src.length);
	}

	public static <T> ArrayIterable<T> wrap(T[] src, int offset, int length)
	{
		if((offset + length) > src.length)
			throw new IndexOutOfBoundsException();
		return new ArrayIterable<T>(src, offset, length);
	}
}
