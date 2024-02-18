package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public interface BuilderContext {
	@Nullable
	public IEnergyStorage getEnergyStorage();
	public BuilderBOMProvider getBOMProvider();
	public BuilderMaterialProvider getMaterialProvider();
	public boolean isEnergyRequired();
	public boolean areItemsRequired();
}
