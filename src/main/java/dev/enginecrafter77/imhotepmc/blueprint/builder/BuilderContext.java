package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface BuilderContext {
	public World getWorld();
	@Nullable
	public IEnergyStorage getEnergyStorage();
	public IItemHandler getMaterialProvider();
}
