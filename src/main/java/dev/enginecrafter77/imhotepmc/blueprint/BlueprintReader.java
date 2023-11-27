package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.blueprint.iter.BlueprintVoxel;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Iterator;

public interface BlueprintReader extends Iterator<BlueprintVoxel> {
	public NBTTagCompound saveReaderState();
	public void restoreReaderState(NBTTagCompound tag);
}
