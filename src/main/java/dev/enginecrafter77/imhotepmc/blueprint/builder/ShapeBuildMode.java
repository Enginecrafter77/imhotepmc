package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.NaturalVoxelIndexer;
import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import dev.enginecrafter77.imhotepmc.util.BlockPosBox;
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

	public BuilderTask createShapeTask(BuilderHandler handler, World world, BlockPos pos)
	{
		switch(this)
		{
		case BUILD:
			return handler.createTemplateTask(world, pos);
		case CLEAR:
			return handler.createClearTask(world, pos);
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
