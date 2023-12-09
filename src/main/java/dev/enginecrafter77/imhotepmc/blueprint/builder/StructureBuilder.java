package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.SaveableStateHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface StructureBuilder extends SaveableStateHolder<NBTTagCompound> {
	public boolean nextTask(World world);

	@Nullable
	public BuilderTask getLastTask(World world);
}
