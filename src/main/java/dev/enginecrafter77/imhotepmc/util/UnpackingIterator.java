package dev.enginecrafter77.imhotepmc.util;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UnpackingIterator<I extends Iterable<O>, O> implements Iterator<O> {
	private final List<Iterator<O>> iterators;
	private int index;

	public UnpackingIterator(Iterable<? extends I> iterable)
	{
		this.iterators = StreamSupport.stream(iterable.spliterator(), false).map(Iterable::iterator).collect(Collectors.toList());
		this.index = -1;
	}

	private int findNextNonEmptyIteratorIndex()
	{
		int start = this.index + 1;
		while(start < this.iterators.size() && !this.iterators.get(start).hasNext())
			++start;
		return start;
	}

	private boolean shouldContinueCurrentIterator()
	{
		if(this.index < 0)
			return false;
		return this.iterators.get(this.index).hasNext();
	}

	@Override
	public boolean hasNext()
	{
		if(this.shouldContinueCurrentIterator())
			return true;

		int next = this.findNextNonEmptyIteratorIndex();
		return next < this.iterators.size();
	}

	@Override
	public O next()
	{
		if(!this.shouldContinueCurrentIterator())
			this.index = this.findNextNonEmptyIteratorIndex();
		return this.iterators.get(this.index).next();
	}
}
