package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.nbt.NBTBase;

public interface SaveableStateHolder<T extends NBTBase> {
	public T saveState();
	public void restoreState(T nbt);
}
