package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.BlockPos;

public interface BlockPosIndexer {
	public BlockPos fromIndex(int index);
	public int toIndex(BlockPos index);
	public int getVolume();
}
