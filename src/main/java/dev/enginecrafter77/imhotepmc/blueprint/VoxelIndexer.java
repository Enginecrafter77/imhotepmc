package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.BlockPos;

public interface VoxelIndexer {
	public BlockPos fromIndex(int index);
	public int toIndex(BlockPos index);
	public int getVolume();
}
