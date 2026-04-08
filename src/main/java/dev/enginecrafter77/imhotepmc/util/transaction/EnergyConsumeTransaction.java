package dev.enginecrafter77.imhotepmc.util.transaction;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyConsumeTransaction implements Transaction {
	private final IEnergyStorage provider;
	private final int amount;

	public EnergyConsumeTransaction(IEnergyStorage provider, int amount)
	{
		this.provider = provider;
		this.amount = amount;
	}

	@Override
	public boolean canCommit()
	{
		int received = this.provider.extractEnergy(this.amount, true);
		return received == this.amount;
	}

	@Override
	public void commit()
	{
		this.provider.extractEnergy(this.amount, false);
	}
}
