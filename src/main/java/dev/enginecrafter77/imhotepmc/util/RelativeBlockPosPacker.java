package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.BlockPos;

public class RelativeBlockPosPacker {
	private static final int BIAS_X = 1024;
	private static final int BIAS_Y = 512;
	private static final int BIAS_Z = 1024;

	private BlockPos origin;

	public RelativeBlockPosPacker(BlockPos origin)
	{
		this.origin = origin;
	}

	public RelativeBlockPosPacker()
	{
		this(BlockPos.ORIGIN);
	}

	public void setOrigin(BlockPos origin)
	{
		this.origin = origin;
	}

	public BlockPos getOrigin()
	{
		return this.origin;
	}

	public int pack(BlockPos pos)
	{
		return RelativeBlockPosPacker.packRelativeBlockPos(pos.subtract(this.origin));
	}

	public BlockPos unpack(int data)
	{
		return RelativeBlockPosPacker.unpackRelativeBlockPos(data).add(this.origin);
	}

	public boolean canPack(BlockPos pos)
	{
		return RelativeBlockPosPacker.canRelativeBlockPosBePacked(pos.subtract(this.origin));
	}

	public static boolean canRelativeBlockPosBePacked(BlockPos relative)
	{
		return Math.abs(relative.getX()) < 1024 && Math.abs(relative.getY()) < 512 && Math.abs(relative.getZ()) < 1024;
	}

	public static int packRelativeBlockPos(BlockPos source)
	{
		if(!canRelativeBlockPosBePacked(source))
			throw new IllegalArgumentException("Block pos out of range");
		int rx = source.getX() + BIAS_X;
		int ry = source.getY() + BIAS_Y;
		int rz = source.getZ() + BIAS_Z;
		return (rx & 0x7FF) | ((ry & 0x3FF) << 11) | ((rz & 0x7FF) << 21);
	}

	public static BlockPos unpackRelativeBlockPos(int data)
	{
		int rx = data & 0x7FF; // 11 bits
		int ry = (data >> 11) & 0x3FF; // 10 bits
		int rz = (data >> 21) & 0x7FF; // 11 bits
		rx -= BIAS_X;
		ry -= BIAS_Y;
		rz -= BIAS_Z;
		return new BlockPos(rx, ry, rz);
	}
}
