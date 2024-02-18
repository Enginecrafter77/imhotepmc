package dev.enginecrafter77.imhotepmc.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class MatchingExtractItemStackTransaction implements ItemStackTransaction {
	private final Predicate<ItemStack> predicate;

	private final List<Integer> matchingSlots;
	private final List<ItemStack> matchingStacks;

	private final int matchLimit;

	@Nullable
	private IItemHandler inventory;

	public MatchingExtractItemStackTransaction(Predicate<ItemStack> predicate)
	{
		this(predicate, -1);
	}

	public MatchingExtractItemStackTransaction(Predicate<ItemStack> predicate, int matchLimit)
	{
		this.matchingStacks = new ArrayList<ItemStack>(4);
		this.matchingSlots = new ArrayList<Integer>(4);
		this.predicate = predicate;
		this.matchLimit = matchLimit;
	}

	@Override
	public void evaluate(IItemHandler inventory)
	{
		this.inventory = inventory;
		this.matchingSlots.clear();
		this.matchingStacks.clear();
		for(int slot = 0; slot < inventory.getSlots(); ++slot)
		{
			ItemStack stack = inventory.getStackInSlot(slot);
			ItemStack extracted = inventory.extractItem(slot, stack.getCount(), true);
			if(!this.predicate.test(extracted))
				continue;
			this.matchingSlots.add(slot);
			this.matchingStacks.add(extracted);
			if(this.matchLimit != -1 && this.matchingSlots.size() >= this.matchLimit)
				break;
		}
	}

	@Override
	public boolean canCommit()
	{
		return true;
	}

	@Override
	public void commit()
	{
		if(this.inventory == null)
			throw new IllegalStateException();
	}

	@Override
	public List<ItemStack> getTransactionStacks()
	{
		return this.matchingStacks;
	}

	@Override
	public List<ItemStack> getBlockingStacks()
	{
		return Collections.emptyList();
	}
}
