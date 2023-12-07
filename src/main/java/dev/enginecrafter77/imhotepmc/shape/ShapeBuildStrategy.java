package dev.enginecrafter77.imhotepmc.shape;

import dev.enginecrafter77.imhotepmc.blueprint.NaturalVoxelIndexer;
import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public enum ShapeBuildStrategy {
	TOP_DOWN,
	BOTTOM_UP;

	public VoxelIndexer createVoxelIndexer(BlockPos origin, Vec3i size)
	{
		switch(this)
		{
		case BOTTOM_UP:
			return new NaturalVoxelIndexer(origin, size);
		case TOP_DOWN:
			return new TopDownIndexer(origin, size);
		default:
			throw new UnsupportedOperationException();
		}
	}
}
