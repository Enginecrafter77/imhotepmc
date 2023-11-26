package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public interface VoxelIndexer {
	public BlockPos fromIndex(int index);
	public int toIndex(BlockPos index);
	public int getVolume();

	public static class VoxelIndexOutOfBoundsException extends IndexOutOfBoundsException
	{
		public VoxelIndexOutOfBoundsException(int index, int max)
		{
			super(String.format("Voxel must be 0 <= %d < %d)", index, max));
		}

		public VoxelIndexOutOfBoundsException(BlockPos pos, Vec3i size)
		{
			super(String.format("Block position %s not in bounds %s", pos, size));
		}
	}
}
