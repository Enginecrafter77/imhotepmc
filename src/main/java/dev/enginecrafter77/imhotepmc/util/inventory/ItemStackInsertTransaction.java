package dev.enginecrafter77.imhotepmc.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ItemStackInsertTransaction extends AbstractItemStackTransaction {
	public ItemStackInsertTransaction(Collection<ItemStack> stacks)
	{
		super(stacks);
	}

	@Override
	protected boolean isSlotSuitable(@Nonnull IItemHandler inventory, ItemStack stack, int slot)
	{
		return inventory.insertItem(slot, stack, true).isEmpty();
	}

	@Override
	protected void performSlotOperation(@Nonnull IItemHandler inventory, ItemStack stack, int slot)
	{
		inventory.insertItem(slot, stack, false);
	}
}
