package dev.enginecrafter77.imhotepmc.util.energy;

import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyTransaction {
	public int getAmount();
	public void evaluate(IEnergyStorage energyStorage);
	public boolean canCommit();
	public void commit();
}
