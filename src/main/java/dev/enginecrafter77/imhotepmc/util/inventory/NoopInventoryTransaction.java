package dev.enginecrafter77.imhotepmc.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.Collection;
import java.util.Collections;

public final class NoopInventoryTransaction implements ItemStackTransaction {
	private static final NoopInventoryTransaction INSTANCE = new NoopInventoryTransaction();

	private NoopInventoryTransaction() {}

	@Override
	public Collection<ItemStack> getTransactionStacks()
	{
		return Collections.emptyList();
	}

	@Override
	public Collection<ItemStack> getBlockingStacks()
	{
		return Collections.emptyList();
	}

	@Override
	public void evaluate(IItemHandler inventory) {}

	@Override
	public boolean canCommit()
	{
		return true;
	}

	@Override
	public void commit() {}

	public static NoopInventoryTransaction getInstance()
	{
		return INSTANCE;
	}
}
