package dev.enginecrafter77.imhotepmc.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.AbstractList;

public class RelativeBlockPosList extends AbstractList<BlockPos> implements INBTSerializable<NBTTagIntArray> {
	private static final int BIAS_X = 1024;
	private static final int BIAS_Y = 512;
	private static final int BIAS_Z = 1024;

	private final IntList ints;
	private BlockPos origin;

	public RelativeBlockPosList(BlockPos origin)
	{
		this.origin = origin;
		this.ints = new IntArrayList();
	}

	public void reset(BlockPos anchor)
	{
		this.clear();
		this.origin = anchor;
	}

	public BlockPos getOrigin()
	{
		return this.origin;
	}

	@Override
	public void clear()
	{
		this.ints.clear();
	}

	@Override
	public BlockPos remove(int index)
	{
		return unpackBlockPos(this.ints.remove(index));
	}

	@Override
	public BlockPos get(int index)
	{
		return this.origin.add(unpackBlockPos(this.ints.get(index)));
	}

	@Override
	public void add(int index, BlockPos element)
	{
		this.ints.add(index, packBlockPos(element.subtract(this.origin)));
	}

	public boolean canAdd(BlockPos element)
	{
		return canBlockPosBePacked(element.subtract(this.origin));
	}

	@Override
	public BlockPos set(int index, BlockPos element)
	{
		int prev = this.ints.set(index, packBlockPos(element.subtract(this.origin)));
		return unpackBlockPos(prev);
	}

	@Override
	public int size()
	{
		return this.ints.size();
	}

	public static boolean canBlockPosBePacked(BlockPos relative)
	{
		return Math.abs(relative.getX()) < 1024 && Math.abs(relative.getY()) < 512 && Math.abs(relative.getZ()) < 1024;
	}

	public static int packBlockPos(BlockPos source)
	{
		if(!canBlockPosBePacked(source))
			throw new IllegalArgumentException("Block pos out of range");
		int rx = source.getX() + BIAS_X;
		int ry = source.getY() + BIAS_Y;
		int rz = source.getZ() + BIAS_Z;
		return (rx & 0x7FF) | ((ry & 0x3FF) << 11) | ((rz & 0x7FF) << 21);
	}

	public static BlockPos unpackBlockPos(int data)
	{
		int rx = data & 0x7FF; // 11 bits
		int ry = (data >> 11) & 0x3FF; // 10 bits
		int rz = (data >> 21) & 0x7FF; // 11 bits
		rx -= BIAS_X;
		ry -= BIAS_Y;
		rz -= BIAS_Z;
		return new BlockPos(rx, ry, rz);
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
