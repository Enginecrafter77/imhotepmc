package dev.enginecrafter77.imhotepmc.util;

import java.util.*;

public class ArrayWrapperList<T> implements List<T> {
	private final T[] src;
	private final int offset;
	private final int length;

	public ArrayWrapperList(T[] src, int offset, int length)
	{
		this.src = src;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public int size()
	{
		return this.length;
	}

	@Override
	public boolean isEmpty()
	{
		return this.length == 0;
	}

	@Override
	public boolean contains(Object o)
	{
		for(int index = 0; index < this.length; ++index)
		{
			T val = this.src[this.offset + index];
			if(Objects.equals(o, val))
				return true;
		}
		return false;
	}

	@Override
	public Iterator<T> iterator()
	{
		return new ArrayIterator<T>(this.src, this.offset, this.length, 0);
	}

	@Override
	public T[] toArray()
	{
		return this.src;
	}

	@Override
	@SuppressWarnings("SuspiciousSystemArraycopy") // this method is fd up
	public <T1> T1[] toArray(T1[] a)
	{
		System.arraycopy(this.src, this.offset, a, 0, this.length);
		return a;
	}

	@Override
	public boolean add(T t)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		int cont = 0;
		for(Object o : c)
		{
			if(this.contains(o))
				++cont;
		}
		return cont >= c.size();
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {}

	@Override
	public T get(int index)
	{
		if(index < 0 || index >= this.length)
			throw new ArrayIndexOutOfBoundsException(index);
		return this.src[this.offset + index];
	}

	@Override
	public T set(int index, T element)
	{
		if(index < 0 || index >= this.length)
			throw new ArrayIndexOutOfBoundsException(index);
		index += this.offset;
		T val = this.src[index];
		this.src[index] = element;
		return val;
	}

	@Override
	public void add(int index, T element)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o)
	{
		return 0;
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return 0;
	}

	@Override
	public ListIterator<T> listIterator()
	{
		return new ArrayIterator<T>(this.src, this.offset, this.length, 0);
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		return new ArrayIterator<T>(this.src, this.offset, this.length, index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		return new ArrayWrapperList<T>(this.src, fromIndex, toIndex - fromIndex);
	}

	public static <T> ArrayWrapperList<T> of(T[] src, int offset, int length)
	{
		return new ArrayWrapperList<T>(src, offset, length);
	}

	public static <T> ArrayWrapperList<T> of(T[] src)
	{
		return ArrayWrapperList.of(src, 0, src.length);
	}
}
