package dev.enginecrafter77.imhotepmc.util.transaction;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyTransferTransaction implements Transaction {
	private final IEnergyStorage source;
	private final IEnergyStorage drain;
	private final int amount;
	private final boolean allowPartial;

	public EnergyTransferTransaction(IEnergyStorage source, IEnergyStorage drain, int amount, boolean allowPartial)
	{
		this.source = source;
		this.drain = drain;
		this.amount = amount;
		this.allowPartial = allowPartial;
	}

	@Override
	public boolean canCommit()
	{
		int wouldTransfer = this.source.extractEnergy(this.amount, true);
		if(!this.allowPartial && wouldTransfer < this.amount)
			return false;
		int wouldReceive = this.drain.receiveEnergy(wouldTransfer, true);
		return this.allowPartial || wouldReceive == wouldTransfer;
	}

	@Override
	public void commit()
	{
		int extracted = this.source.extractEnergy(this.amount, true);
		int received = this.drain.receiveEnergy(extracted, true);
		this.source.extractEnergy(received, false);
		this.drain.receiveEnergy(received, false);
	}
}
