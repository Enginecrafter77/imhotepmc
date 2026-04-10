package dev.enginecrafter77.imhotepmc.util;

import com.google.common.base.Predicates;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GraphBlockIterator implements Iterator<BlockPos> {
	private final GraphIterationMethod method;
	private final BlockExpandFunction expandFunction;

	private final Predicate<BlockPos> filter;
	private final Set<BlockPos> explored; // acts also as the closed queue
	private final Deque<BlockPos> open;

	public GraphBlockIterator(GraphIterationMethod method, BlockExpandFunction expandFunction, @Nullable Predicate<BlockPos> filter)
	{
		if(filter == null)
			filter = Predicates.alwaysTrue();

		this.expandFunction = expandFunction;
		this.method = method;
		this.filter = filter;
		this.explored = new TreeSet<>();
		this.open = new LinkedList<>();
	}

	void prime(BlockPos start)
	{
		if(!this.filter.test(start))
			return;
		this.explored.clear();
		this.open.clear();
		this.explored.add(start);
		this.open.add(start);
	}

	@Override
	public boolean hasNext()
	{
		return !this.open.isEmpty();
	}

	@Override
	public BlockPos next()
	{
		BlockPos next = this.method.pop(this.open);
		this.expandFunction.expand(next)
				.filter(b -> !this.explored.contains(b))
				.filter(this.filter)
				.forEach(b -> {
					this.method.push(this.open, b);
					this.explored.add(b);
				});
		return next;
	}

	@Nullable
	public BlockPos peek()
	{
		return this.method.peek(this.open);
	}

	public static GraphBlockIteratorBuilder bfs()
	{
		return (new GraphBlockIteratorBuilder()).using(GraphIterationMethod.BFS);
	}

	public static GraphBlockIteratorBuilder dfs()
	{
		return (new GraphBlockIteratorBuilder()).using(GraphIterationMethod.DFS);
	}

	public static enum GraphIterationMethod
	{
		BFS,
		DFS;

		public void push(Deque<BlockPos> deque, BlockPos item)
		{
			deque.addLast(item);
		}

		public BlockPos pop(Deque<BlockPos> deque)
		{
			switch(this)
			{
			case BFS:
				return deque.removeFirst();
			case DFS:
				return deque.removeLast();
			default:
				throw new UnsupportedOperationException();
			}
		}

		@Nullable
		public BlockPos peek(Deque<BlockPos> deque)
		{
			switch(this)
			{
			case BFS:
				return deque.peekFirst();
			case DFS:
				return deque.peekLast();
			default:
				throw new UnsupportedOperationException();
			}
		}
	}

	public static interface BlockExpandFunction {
		public static final BlockExpandFunction ALL = s -> Stream.of(
				s.north(),
				s.east(),
				s.south(),
				s.west(),
				s.up(),
				s.down()
		);

		public static final BlockExpandFunction HPLANE = s -> Stream.of(
				s.north(),
				s.east(),
				s.south(),
				s.west()
		);

		public Stream<BlockPos> expand(BlockPos start);
	}

	public static class GraphBlockIteratorBuilder
	{
		private GraphIterationMethod method;
		private BlockExpandFunction expandFunction;
		@Nullable
		private Predicate<BlockPos> filter;
		@Nullable
		private BlockPos start;

		public GraphBlockIteratorBuilder()
		{
			this.method = GraphIterationMethod.BFS;
			this.expandFunction = BlockExpandFunction.ALL;
			this.filter = null;
			this.start = null;
		}

		public GraphBlockIteratorBuilder using(GraphIterationMethod method)
		{
			this.method = method;
			return this;
		}

		public GraphBlockIteratorBuilder by(BlockExpandFunction expandFunction)
		{
			this.expandFunction = expandFunction;
			return this;
		}

		public GraphBlockIteratorBuilder filter(Predicate<BlockPos> filter)
		{
			if(this.filter == null)
			{
				this.filter = filter;
			}
			else
			{
				this.filter = this.filter.and(filter);
			}
			return this;
		}

		public GraphBlockIteratorBuilder startingAt(BlockPos start)
		{
			this.start = start;
			return this;
		}

		public GraphBlockIterator build()
		{
			GraphBlockIterator iterator = new GraphBlockIterator(this.method, this.expandFunction, this.filter);
			if(this.start != null)
				iterator.prime(this.start);
			return iterator;
		}
	}
}
