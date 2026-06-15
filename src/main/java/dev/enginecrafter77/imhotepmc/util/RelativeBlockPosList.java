package dev.enginecrafter77.imhotepmc.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.AbstractList;

public class RelativeBlockPosList extends AbstractList<BlockPos> implements INBTSerializable<NBTTagIntArray> {
	private final RelativeBlockPosPacker packer;
	private final IntList ints;

	public RelativeBlockPosList(BlockPos origin)
	{
		this.packer = new RelativeBlockPosPacker(origin);
		this.ints = new IntArrayList();
	}

	public void reset(BlockPos anchor)
	{
		this.clear();
		this.packer.setOrigin(anchor);
	}

	public BlockPos getOrigin()
	{
		return this.packer.getOrigin();
	}

	@Override
	public void clear()
	{
		this.ints.clear();
	}

	@Override
	public BlockPos remove(int index)
	{
		return this.packer.unpack(this.ints.remove(index));
	}

	@Override
	public BlockPos get(int index)
	{
		return this.packer.unpack(this.ints.get(index));
	}

	@Override
	public void add(int index, BlockPos element)
	{
		this.ints.add(index, this.packer.pack(element));
	}

	public boolean canAdd(BlockPos element)
	{
		return this.packer.canPack(element);
	}

	@Override
	public BlockPos set(int index, BlockPos element)
	{
		int prev = this.ints.set(index, this.packer.pack(element));
		return this.packer.unpack(prev);
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
}
