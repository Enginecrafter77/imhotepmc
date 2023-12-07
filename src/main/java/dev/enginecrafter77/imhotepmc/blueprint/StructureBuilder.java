package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.util.SaveableStateHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface StructureBuilder extends SaveableStateHolder<NBTTagCompound> {
	public boolean isReady();
	public boolean isFinished();

	public void tryPlaceNextBlock(World world);
}
