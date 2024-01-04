package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemStackReclaimTransaction extends AbstractItemStackTransaction {
	public ItemStackReclaimTransaction(IItemHandler inv, Collection<ItemStack> stacks)
	{
		super(inv, stacks);
	}

	@Override
	protected Collection<ItemStack> calculateBlocking(Collection<ItemStack> stacks, IItemHandler inventory)
	{
		List<ItemStack> overflowing = new ArrayList<ItemStack>(stacks.size());
		for(ItemStack stack : this.getTransactionStacks())
		{
			int slot = this.findSlotForInsert(stack);
			if(slot == -1)
				overflowing.add(stack);
		}
		return overflowing;
	}

	@Override
	public void commit()
	{
		for(ItemStack stack : this.getTransactionStacks())
		{
			int slot = this.findSlotForInsert(stack);
			if(slot == -1)
				continue;
			this.getSourceInventory().insertItem(slot, stack, false);
		}
	}

	protected int findSlotForInsert(ItemStack stack)
	{
		return this.findSlot((ItemStack stackInSlot) -> stackInSlot.isEmpty() || (ItemStack.areItemsEqual(stack, stackInSlot) && (stackInSlot.getCount() + stack.getCount()) <= stackInSlot.getItem().getItemStackLimit(stackInSlot)));
	}
}
