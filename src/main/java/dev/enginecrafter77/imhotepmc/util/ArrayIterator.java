package dev.enginecrafter77.imhotepmc.util;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {
	private final T[] src;
	private final int offset;
	private final int length;

	private int index;

	public ArrayIterator(T[] src, int offset, int length)
	{
		this.src = src;
		this.offset = offset;
		this.length = length;
		this.index = -1;
	}

	protected int getTrueIndex()
	{
		return this.offset + this.index;
	}

	@Override
	public boolean hasNext()
	{
		return (this.index + 1) < this.length;
	}

	@Override
	public T next()
	{
		++this.index;
		return this.src[this.getTrueIndex()];
	}
}
