package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface ItemStackTransaction {
	public Collection<ItemStack> getTransactionStacks();
	public Collection<ItemStack> getBlockingStacks();
	public boolean isCommitable();
	public void commit();
}
