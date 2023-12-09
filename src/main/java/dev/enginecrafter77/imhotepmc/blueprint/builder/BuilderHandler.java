package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BuilderHandler {
	public BuilderTask createPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details);
	public BuilderTask createTemplateTask(World world, BlockPos pos);
	public BuilderTask createClearTask(World world, BlockPos pos);
}
