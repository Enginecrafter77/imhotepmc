package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.NaturalVoxelIndexer;
import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import dev.enginecrafter77.imhotepmc.util.BlockPosBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public enum ShapeBuildMode implements ShapeBuildStrategy {
	BUILD,
	CLEAR;

	public BuilderAction getBuilderAction(BuilderHost host)
	{
		switch(this)
		{
		case BUILD:
			return BuilderAction.PLACE;
		case CLEAR:
			return BuilderAction.CLEAR;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public VoxelIndexer createVoxelIndexer(BlockPosBox box)
	{
		BlockPos origin = box.getMinCorner();
		Vec3i size = box.getSize();
		switch(this)
		{
		case BUILD:
			return new NaturalVoxelIndexer(origin, size);
		case CLEAR:
			return new TopDownIndexer(origin, size);
		default:
			throw new UnsupportedOperationException();
		}
	}
}
