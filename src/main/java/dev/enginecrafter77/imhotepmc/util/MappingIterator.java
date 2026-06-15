package dev.enginecrafter77.imhotepmc.util;

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterator<I, O> implements Iterator<O> {
	private final Iterator<I> in;
	private final Function<I, O> mapper;

	public MappingIterator(Iterator<I> in, Function<I, O> mapper)
	{
		this.in = in;
		this.mapper = mapper;
	}

	@Override
	public boolean hasNext()
	{
		return this.in.hasNext();
	}

	@Override
	public O next()
	{
		return this.mapper.apply(this.in.next());
	}
}
