package dev.enginecrafter77.imhotepmc.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ImmutableListCollector<T> implements Collector<T, ArrayList<T>, ImmutableList<T>> {
	@Override
	public Supplier<ArrayList<T>> supplier()
	{
		return ArrayList::new;
	}

	@Override
	public BiConsumer<ArrayList<T>, T> accumulator()
	{
		return ArrayList::add;
	}

	@Override
	public BinaryOperator<ArrayList<T>> combiner()
	{
		return (ArrayList<T> first, ArrayList<T> second) -> {
			first.addAll(second);
			return first;
		};
	}

	@Override
	public Function<ArrayList<T>, ImmutableList<T>> finisher()
	{
		return ImmutableList::copyOf;
	}

	@Override
	public Set<Characteristics> characteristics()
	{
		return ImmutableSet.of();
	}

	public static <T> ImmutableListCollector<T> get()
	{
		return new ImmutableListCollector<T>();
	}
}
