package dev.enginecrafter77.imhotepmc.util.inventory;

import net.minecraftforge.items.IItemHandler;

public interface ItemStackTransaction extends ItemStackTransactionView {
	public void evaluate(IItemHandler inventory);
	public boolean canCommit();
	public void commit();
}
