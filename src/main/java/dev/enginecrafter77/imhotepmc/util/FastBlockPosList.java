package dev.enginecrafter77.imhotepmc.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.AbstractList;

public class FastBlockPosList extends AbstractList<BlockPos> implements INBTSerializable<NBTTagIntArray> {
	private final IntList ints;

	public FastBlockPosList()
	{
		this.ints = new IntArrayList();
	}

	@Override
	public void clear()
	{
		this.ints.clear();
	}

	@Override
	public BlockPos remove(int index)
	{
		return BlockPosIntegerPacker.unpackRelativeBlockPos(this.ints.remove(index));
	}

	@Override
	public BlockPos get(int index)
	{
		return BlockPosIntegerPacker.unpackRelativeBlockPos(this.ints.get(index));
	}

	@Override
	public void add(int index, BlockPos element)
	{
		this.ints.add(index, BlockPosIntegerPacker.packRelativeBlockPos(element));
	}

	public boolean canAdd(BlockPos element)
	{
		return BlockPosIntegerPacker.canRelativeBlockPosBePacked(element);
	}

	@Override
	public BlockPos set(int index, BlockPos element)
	{
		int prev = this.ints.set(index, BlockPosIntegerPacker.packRelativeBlockPos(element));
		return BlockPosIntegerPacker.unpackRelativeBlockPos(prev);
	}

	@Override
	public int size()
	{
		return this.ints.size();
	}

	@Override
	public NBTTagIntArray serializeNBT()
	{
		return new NBTTagIntArray(this.ints.toIntArray());
	}

	@Override
	public void deserializeNBT(NBTTagIntArray nbt)
	{
		this.ints.clear();
		this.ints.addElements(0, nbt.getIntArray());
	}

	public AbsolutePositionAdapter relativeTo(BlockPos origin)
	{
		return new AbsolutePositionAdapter(origin);
	}

	public class AbsolutePositionAdapter extends AbstractList<BlockPos>
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
		public void clear()
		{
			FastBlockPosList.this.clear();
		}

		@Override
		public BlockPos remove(int index)
		{
			return FastBlockPosList.this.remove(index).add(this.origin);
		}

		@Override
		public BlockPos get(int index)
		{
			return FastBlockPosList.this.get(index).add(this.origin);
		}

		@Override
		public void add(int index, BlockPos element)
		{
			FastBlockPosList.this.add(index, element.subtract(this.origin));
		}

		public boolean canAdd(BlockPos element)
		{
			return FastBlockPosList.this.canAdd(element.subtract(this.origin));
		}

		@Override
		public BlockPos set(int index, BlockPos element)
		{
			return FastBlockPosList.this.set(index, element.subtract(this.origin)).add(this.origin);
		}

		@Override
		public int size()
		{
			return FastBlockPosList.this.size();
		}
	}
}
