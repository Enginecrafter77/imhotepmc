package dev.enginecrafter77.imhotepmc.util;

import java.util.Iterator;
import java.util.List;

public class CombiningIterator<T1, T2> implements Iterator<CombiningIterator.Pair<T1, T2>> {
	private final List<T1> src1;
	private final List<T2> src2;

	private final MutablePair<T1, T2> pair;
	private int i1, i2;

	public CombiningIterator(List<T1> src1, List<T2> src2)
	{
		this.pair = new MutablePair<T1, T2>();
		this.src1 = src1;
		this.src2 = src2;
		this.i1 = 0;
		this.i2 = -1;

		if(src1 == src2)
			++this.i2;
	}

	@Override
	public boolean hasNext()
	{
		if(this.src1 == this.src2)
			return (this.i1 + 2) < this.src1.size();

		return (this.i1 + 1) < this.src1.size() || (this.i2 + 1) < this.src2.size();
	}

	@Override
	public Pair<T1, T2> next()
	{
		if((this.i2 + 1) == this.src2.size())
		{
			++this.i1;

			if(this.src1 == this.src2)
				this.i2 = this.i1;
			else
				this.i2 = -1;
		}
		++this.i2;
		this.pair.set(this.src1.get(this.i1), this.src2.get(this.i2));
		return this.pair;
	}

	public static <T> Iterable<Pair<T, T>> selfCombinations(List<T> src)
	{
		return () -> new CombiningIterator<T, T>(src, src);
	}

	public static <T1, T2> Iterable<Pair<T1, T2>> combinations(List<T1> src1, List<T2> src2)
	{
		return () -> new CombiningIterator<T1, T2>(src1, src2);
	}

	public static interface Pair<T1, T2>
	{
		public T1 getFirst();
		public T2 getSecond();
	}

	public static class MutablePair<T1, T2> implements Pair<T1, T2>
	{
		private T1 first;
		private T2 second;

		public MutablePair()
		{
			this.first = null;
			this.second = null;
		}

		public void set(T1 first, T2 second)
		{
			this.first = first;
			this.second = second;
		}

		@Override
		public T1 getFirst()
		{
			return this.first;
		}

		@Override
		public T2 getSecond()
		{
			return this.second;
		}
	}
}
