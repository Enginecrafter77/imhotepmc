package dev.enginecrafter77.imhotepmc.util;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.List;

public class CombiningIterator<T1, T2> implements Iterator<Pair<T1, T2>> {
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
		if(this.src1.isEmpty() || this.src2.isEmpty())
			return false;

		int i1inc = this.src1 == this.src2 ? 2 : 1; // The space required after i1; 2 for self combinations, 1 for cross combination
		return (this.i1 + i1inc) < this.src1.size() || (this.i2 + 1) < this.src2.size();
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
		this.pair.setLeft(this.src1.get(this.i1));
		this.pair.setRight(this.src2.get(this.i2));
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
}
