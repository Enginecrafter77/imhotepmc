package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public interface ShapeBuildStrategy {
	public VoxelIndexer createVoxelIndexer(BlockPos origin, Vec3i size);
}
