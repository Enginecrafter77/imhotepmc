package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class NaturalVoxelIndexer implements VoxelIndexer {
	private final BlockPos origin;
	private final Vec3i size;
	private final int volume;
	private final int floor;
	private final int row;

	public NaturalVoxelIndexer(BlockPos origin, Vec3i size)
	{
		if(size.getX() == 0 || size.getY() == 0 || size.getZ() == 0)
			throw new IllegalArgumentException("Attempting to create indexer of volume 0!");
		this.size = size;
		this.origin = origin;
		this.volume = size.getY() * size.getX() * size.getZ();
		this.floor = size.getX() * size.getZ();
		this.row = size.getX();
	}

	@Override
	public BlockPos fromIndex(int index)
	{
		if(index < 0 || index >= this.volume)
			throw new VoxelIndexOutOfBoundsException(index, this.volume);

		int y = index / this.floor;
		int f = index % this.floor;

		int z = f / this.row;
		int x = f % this.row;

		return new BlockPos(this.origin.getX() + x, this.origin.getY() + y, this.origin.getZ() + z);
	}

	@Override
	public int toIndex(BlockPos pos)
	{
		int dx = pos.getX() - this.origin.getX();
		int dy = pos.getY() - this.origin.getY();
		int dz = pos.getZ() - this.origin.getZ();

		if(dx >= this.size.getX() || dy >= this.size.getY() || dz >= this.size.getZ())
			throw new VoxelIndexOutOfBoundsException(pos, this.size);

		return dx + dz * this.row + dy * this.floor;
	}

	@Override
	public int getVolume()
	{
		return this.volume;
	}

	public static NaturalVoxelIndexer inRange(BlockPos from, BlockPos to)
	{
		Vec3i size = VecUtil.difference(to, from);
		return new NaturalVoxelIndexer(from, size);
	}

	public static NaturalVoxelIndexer inVolume(Vec3i size)
	{
		return new NaturalVoxelIndexer(BlockPos.ORIGIN, size);
	}
}
