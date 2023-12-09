package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface BuilderHandler {
	public BuilderTask createPlaceTask(World world, BlockPos pos, Block block, @Nullable NBTTagCompound tileSavedData);
	public BuilderTask createTemplateTask(World world, BlockPos pos);
	public BuilderTask createClearTask(World world, BlockPos pos);
}
