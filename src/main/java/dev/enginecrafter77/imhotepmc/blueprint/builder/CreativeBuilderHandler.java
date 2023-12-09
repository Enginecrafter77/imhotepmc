package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CreativeBuilderHandler implements BuilderHandler {
	private final Block templateBlock;

	public CreativeBuilderHandler(Block templateBlock)
	{
		this.templateBlock = templateBlock;
	}

	@Override
	public BuilderTask createPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details)
	{
		return new BaseBuilderPlaceTask(world, pos, details);
	}

	@Override
	public BuilderTask createTemplateTask(World world, BlockPos pos)
	{
		return new BaseBuilderTemplateTask(world, pos) {
			@Nonnull
			@Override
			public Block getBlockToPlace()
			{
				return templateBlock;
			}
		};
	}

	@Override
	public BuilderTask createClearTask(World world, BlockPos pos)
	{
		return new BaseBuilderClearTask(world, pos);
	}
}
