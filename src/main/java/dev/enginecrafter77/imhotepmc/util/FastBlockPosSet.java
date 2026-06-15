package dev.enginecrafter77.imhotepmc.util;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.minecraft.util.math.BlockPos;

import java.util.AbstractSet;
import java.util.Iterator;

public class FastBlockPosSet extends AbstractSet<BlockPos> {
	private final IntRBTreeSet blocks;

	public FastBlockPosSet()
	{
		this.blocks = new IntRBTreeSet();
	}

	@Override
	public boolean add(BlockPos pos)
	{
		return this.blocks.add(BlockPosIntegerPacker.packRelativeBlockPos(pos));
	}

	public boolean canAdd(BlockPos pos)
	{
		return BlockPosIntegerPacker.canRelativeBlockPosBePacked(pos);
	}

	@Override
	public void clear()
	{
		this.blocks.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		if(!(o instanceof BlockPos))
			return false;
		return this.blocks.contains(BlockPosIntegerPacker.packRelativeBlockPos((BlockPos)o));
	}

	@Override
	public Iterator<BlockPos> iterator()
	{
		return new MappingIterator<Integer, BlockPos>(this.blocks.iterator(), BlockPosIntegerPacker::unpackRelativeBlockPos);
	}

	@Override
	public int size()
	{
		return this.blocks.size();
	}

	public AbsolutePositionAdapter relativeTo(BlockPos pos)
	{
		return new AbsolutePositionAdapter(pos);
	}

	public class AbsolutePositionAdapter extends AbstractSet<BlockPos>
	{
		private final BlockPos origin;

		public AbsolutePositionAdapter(BlockPos origin)
		{
			this.origin = origin;
		}

		public BlockPos getOrigin()
		{
			return this.origin;
		}

		@Override
		public boolean add(BlockPos pos)
		{
			return FastBlockPosSet.this.add(pos.subtract(this.origin));
		}

		public boolean canAdd(BlockPos pos)
		{
			return FastBlockPosSet.this.canAdd(pos.subtract(this.origin));
		}

		@Override
		public void clear()
		{
			FastBlockPosSet.this.clear();
		}

		@Override
		public boolean contains(Object o)
		{
			if(!(o instanceof BlockPos))
				return false;
			BlockPos absolute = (BlockPos)o;
			return FastBlockPosSet.this.contains(absolute.subtract(this.origin));
		}

		@Override
		public Iterator<BlockPos> iterator()
		{
			return new MappingIterator<BlockPos, BlockPos>(FastBlockPosSet.this.iterator(), r -> r.add(this.origin));
		}

		@Override
		public int size()
		{
			return FastBlockPosSet.this.size();
		}
	}
}
