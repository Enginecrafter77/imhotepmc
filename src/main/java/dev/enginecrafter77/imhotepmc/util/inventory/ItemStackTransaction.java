package dev.enginecrafter77.imhotepmc.util.inventory;

import net.minecraftforge.items.IItemHandler;

@Deprecated
public interface ItemStackTransaction extends ItemStackTransactionView {
	public void evaluate(IItemHandler inventory);
	public boolean canCommit();
	public void commit();
}
