package dev.enginecrafter77.imhotepmc.util.inventory;

import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface ItemStackTransactionView {
	public Collection<ItemStack> getTransactionStacks();
	public Collection<ItemStack> getBlockingStacks();
}
