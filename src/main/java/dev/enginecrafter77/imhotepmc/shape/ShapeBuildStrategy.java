package dev.enginecrafter77.imhotepmc.shape;

import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import dev.enginecrafter77.imhotepmc.util.BlockPosBox;

public interface ShapeBuildStrategy {
	public VoxelIndexer createVoxelIndexer(BlockPosBox box);
}
