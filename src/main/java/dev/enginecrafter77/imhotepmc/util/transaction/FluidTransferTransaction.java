package dev.enginecrafter77.imhotepmc.util.transaction;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidTransferTransaction implements Transaction {
	private final IFluidHandler source;
	private final IFluidHandler drain;
	private final int amount;
	private final boolean allowPartial;

	public FluidTransferTransaction(IFluidHandler source, IFluidHandler drain, int amount, boolean allowPartial)
	{
		this.source = source;
		this.drain = drain;
		this.amount = amount;
		this.allowPartial = allowPartial;
	}

	@Override
	public boolean canCommit()
	{
		FluidStack extracted = this.source.drain(this.amount, false);
		if(extracted == null)
			return false;
		if(!this.allowPartial && extracted.amount < this.amount)
			return false;
		int received = this.drain.fill(extracted, false);
		return this.allowPartial || received == extracted.amount;
	}

	@Override
	public void commit()
	{
		FluidStack extracted = this.source.drain(this.amount, false);
		int filled = this.drain.fill(extracted, false);

		FluidStack frExtracted = this.source.drain(filled, true);
		this.drain.fill(frExtracted, true);
	}
}
