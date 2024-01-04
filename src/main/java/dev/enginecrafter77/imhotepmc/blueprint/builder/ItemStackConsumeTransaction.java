package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemStackConsumeTransaction extends AbstractItemStackTransaction {
	public ItemStackConsumeTransaction(IItemHandler inv, Collection<ItemStack> stacks)
	{
		super(inv, stacks);
	}

	@Override
	protected Collection<ItemStack> calculateBlocking(Collection<ItemStack> stacks, IItemHandler inventory)
	{
		List<ItemStack> list = new ArrayList<ItemStack>(stacks.size());
		for(ItemStack stack : this.getTransactionStacks())
		{
			int slot = this.findSlotForExtract(stack);
			if(slot == -1)
				list.add(stack);
		}
		return list;
	}

	@Override
	public void commit()
	{
		for(ItemStack stack : this.getTransactionStacks())
		{
			int slot = this.findSlotForExtract(stack);
			if(slot == -1)
				continue;
			this.getSourceInventory().extractItem(slot, stack.getCount(), false);
		}
	}

	protected int findSlotForExtract(ItemStack stack)
	{
		return this.findSlot((ItemStack stackInSlot) -> ItemStack.areItemsEqual(stack, stackInSlot));
	}
}
