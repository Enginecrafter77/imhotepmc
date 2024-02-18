package dev.enginecrafter77.imhotepmc.util.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyExtractTransaction implements EnergyTransaction {
	private final int amount;

	private IEnergyStorage energyStorage;
	private int extracted;

	public EnergyExtractTransaction(int amount)
	{
		this.amount = amount;
		this.energyStorage = null;
		this.extracted = 0;
	}

	@Override
	public int getAmount()
	{
		return this.amount;
	}

	@Override
	public void evaluate(IEnergyStorage energyStorage)
	{
		this.energyStorage = energyStorage;
		this.extracted = energyStorage.extractEnergy(this.amount, true);
	}

	@Override
	public boolean canCommit()
	{
		return this.extracted == this.amount;
	}

	@Override
	public void commit()
	{
		this.energyStorage.extractEnergy(this.amount, false);
	}
}
