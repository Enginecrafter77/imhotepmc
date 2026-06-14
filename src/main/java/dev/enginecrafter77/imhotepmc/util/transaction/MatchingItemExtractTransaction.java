package dev.enginecrafter77.imhotepmc.util.transaction;

import com.google.common.base.Predicates;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class MatchingItemExtractTransaction implements Transaction {
	private static final int SLOT_UNDEFINED = -1;
	private static final int SLOT_NOT_FOUND = -2;

	private Predicate<ItemStack> filter;

	@Nullable
	private IItemHandler sourceInventory;
	private int amount;

	@Nullable
	private ItemStack extracted;
	private int sourceSlot;

	public MatchingItemExtractTransaction()
	{
		this.filter = Predicates.alwaysTrue();
		this.sourceInventory = null;
		this.amount = 0;
		this.sourceSlot = -1;
		this.extracted = null;
	}

	public void setFilter(Predicate<ItemStack> filter)
	{
		if(filter == this.filter)
			return;
		this.filter = filter;
		this.invalidate();
	}

	public void setExtractAmount(int amount)
	{
		if(amount == this.amount)
			return;
		this.amount = amount;
		this.invalidate();
	}

	public void setSourceInventory(IItemHandler source)
	{
		if(source == this.sourceInventory)
			return;
		this.sourceInventory = source;
		this.invalidate();
	}

	public void invalidate()
	{
		this.sourceSlot = SLOT_UNDEFINED;
		this.extracted = null;
	}

	private void tryFindSlot()
	{
		assert this.sourceInventory != null;

		this.sourceSlot = SLOT_NOT_FOUND;
		for(int i = 0; i < this.sourceInventory.getSlots(); ++i)
		{
			ItemStack extracted = this.sourceInventory.extractItem(i, this.amount, true);
			if(this.filter.test(extracted))
			{
				this.sourceSlot = i;
				return;
			}
		}
	}

	public ItemStack getExtractedItem()
	{
		if(this.extracted == null)
			throw new NoSuchElementException("Run commit before checking for the extracted item!");
		return this.extracted;
	}

	@Override
	public boolean canCommit()
	{
		if(this.sourceInventory == null)
			return false;
		if(this.sourceSlot == SLOT_UNDEFINED)
			this.tryFindSlot();
		return this.sourceSlot != SLOT_NOT_FOUND;
	}

	@Override
	public void commit()
	{
		if(this.sourceSlot == SLOT_UNDEFINED)
			this.tryFindSlot();
		if(this.sourceSlot == SLOT_NOT_FOUND)
			throw new NoSuchElementException("No matching slot found (check using canCommit before running commit)");
		if(this.sourceInventory == null)
			throw new IllegalStateException("No inventory bound");
		this.extracted = this.sourceInventory.extractItem(this.sourceSlot, this.amount, false);
	}
}
