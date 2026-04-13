package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.NaturalVoxelIndexer;
import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public enum ShapeBuildMode implements ShapeBuildStrategy {
	BUILD,
	CLEAR;

	public boolean wouldTaskBeInVain(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		Block prev = state.getBlock();

		switch(this)
		{
		case BUILD:
			return prev != Blocks.AIR;
		case CLEAR:
			return prev == Blocks.AIR;
		default:
			throw new UnsupportedOperationException();
		}
	}

	public BuilderTask createShapeTask(BuilderContext context, BlockPos pos)
	{
		switch(this)
		{
		case BUILD:
			return new BuilderTemplateTask(context, pos);
		case CLEAR:
			return new BuilderClearTask(context, pos);
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public VoxelIndexer createVoxelIndexer(BlockPos origin, Vec3i size)
	{
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
