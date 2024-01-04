package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public abstract class AbstractItemStackTransaction implements ItemStackTransaction {
	private final Collection<ItemStack> stacks;
	private final IItemHandler inventory;

	private final Collection<ItemStack> blocking;

	public AbstractItemStackTransaction(IItemHandler inventory, Collection<ItemStack> stacks)
	{
		this.inventory = inventory;
		this.stacks = stacks;
		this.blocking = Collections.unmodifiableCollection(this.calculateBlocking(stacks, inventory));
	}

	protected abstract Collection<ItemStack> calculateBlocking(Collection<ItemStack> stacks, IItemHandler inventory);

	@Override
	public Collection<ItemStack> getTransactionStacks()
	{
		return this.stacks;
	}

	@Override
	public Collection<ItemStack> getBlockingStacks()
	{
		return this.blocking;
	}

	@Override
	public boolean isCommitable()
	{
		return this.getBlockingStacks().isEmpty();
	}

	public IItemHandler getSourceInventory()
	{
		return this.inventory;
	}

	protected int findSlot(Predicate<ItemStack> slotMatcher)
	{
		int slots = this.inventory.getSlots();
		for(int slot = 0; slot < slots; ++slot)
		{
			ItemStack stackInSlot = this.inventory.getStackInSlot(slot);
			if(slotMatcher.test(stackInSlot))
				return slot;
		}
		return -1;
	}
}
