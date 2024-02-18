package dev.enginecrafter77.imhotepmc.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ItemStackExtractTransaction extends AbstractItemStackTransaction {
	public ItemStackExtractTransaction(Collection<ItemStack> stacks)
	{
		super(stacks);
	}

	@Override
	protected boolean isSlotSuitable(@Nonnull IItemHandler inventory, ItemStack stack, int slot)
	{
		ItemStack extracted = inventory.extractItem(slot, stack.getCount(), true);
		return ItemStack.areItemStacksEqual(stack, extracted);
	}

	@Override
	protected void performSlotOperation(@Nonnull IItemHandler inventory, ItemStack stack, int slot)
	{
		inventory.extractItem(slot, stack.getCount(), false);
	}
}
