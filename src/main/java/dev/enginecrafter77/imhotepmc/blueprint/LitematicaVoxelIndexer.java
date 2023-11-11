package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class LitematicaVoxelIndexer implements VoxelIndexer {
	private final BlockPos origin;
	private final int volume;
	private final int floor;
	private final int row;

	public LitematicaVoxelIndexer(BlockPos from, BlockPos to)
	{
		this.origin = BlockPos.ORIGIN;
		Vec3i size = VecUtil.difference(from, to);
		if(size.getX() == 0 || size.getY() == 0 || size.getZ() == 0)
			throw new IllegalArgumentException("Attempting to create indexer of volume 0!");

		this.volume = size.getY() * size.getX() * size.getZ();
		this.floor = size.getX() * size.getZ();
		this.row = size.getX();
	}

	public LitematicaVoxelIndexer(Vec3i size)
	{
		if(size.getX() == 0 || size.getY() == 0 || size.getZ() == 0)
			throw new IllegalArgumentException("Attempting to create indexer of volume 0!");

		this.origin = BlockPos.ORIGIN;
		this.volume = size.getY() * size.getX() * size.getZ();
		this.floor = size.getX() * size.getZ();
		this.row = size.getX();
	}

	@Override
	public BlockPos fromIndex(int index)
	{
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
		return dx + dz * this.row + dy * this.floor;
	}

	@Override
	public int getVolume()
	{
		return this.volume;
	}
}
