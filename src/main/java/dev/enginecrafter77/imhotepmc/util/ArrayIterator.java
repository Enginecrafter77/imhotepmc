package dev.enginecrafter77.imhotepmc.util;

import java.util.ListIterator;

public class ArrayIterator<T> implements ListIterator<T> {
	private final T[] src;
	private final int offset;
	private final int length;

	private int index;

	public ArrayIterator(T[] src, int offset, int length, int startAt)
	{
		this.src = src;
		this.offset = offset;
		this.length = length;
		this.index = startAt - 1;
	}

	protected int getTrueIndex()
	{
		return this.offset + this.index;
	}

	@Override
	public boolean hasNext()
	{
		return this.nextIndex() < this.length;
	}

	@Override
	public boolean hasPrevious()
	{
		return this.previousIndex() >= 0;
	}

	@Override
	public T next()
	{
		++this.index;
		return this.src[this.getTrueIndex()];
	}

	@Override
	public T previous()
	{
		--this.index;
		return this.src[this.getTrueIndex()];
	}

	@Override
	public int nextIndex()
	{
		return this.index + 1;
	}

	@Override
	public int previousIndex()
	{
		return this.index - 1;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(T t)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(T t)
	{
		this.src[this.index] = t;
	}
}
