package dev.enginecrafter77.imhotepmc.util.transaction;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.TreeMap;

public class CowInvView implements IItemHandler {
	private final TreeMap<Integer, ItemStack> overrides;
	private final IItemHandler delegate;

	public CowInvView(IItemHandler delegate)
	{
		this.overrides = new TreeMap<>();
		this.delegate = delegate;
	}

	public void reset()
	{
		this.overrides.clear();
	}

	@Override
	public int getSlots()
	{
		return this.delegate.getSlots();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if(this.overrides.containsKey(slot))
			return this.overrides.get(slot);
		return this.delegate.getStackInSlot(slot);
	}

	private ItemStack cowStack(int slot)
	{
		return this.overrides.computeIfAbsent(slot, s -> this.delegate.getStackInSlot(s).copy());
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		ItemStack pre = this.getStackInSlot(slot);
		if(pre.isEmpty())
		{
			if(!simulate)
				this.overrides.put(slot, stack);
			return ItemStack.EMPTY;
		}
		if(!pre.isItemEqual(stack))
			return stack;
		int limit = this.getSlotLimit(slot);
		int toMove = limit - pre.getCount();

		if(!simulate)
			this.cowStack(slot).setCount(pre.getCount() + toMove);

		ItemStack ret = stack.copy();
		ret.setCount(ret.getCount() - toMove);
		return ret;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		ItemStack inSlot = this.getStackInSlot(slot);
		if(inSlot.getCount() < amount)
			return ItemStack.EMPTY;

		ItemStack mod = inSlot.copy();
		ItemStack ext = mod.splitStack(amount);
		if(!simulate)
		{
			if(mod.isEmpty())
				this.overrides.put(slot, ItemStack.EMPTY);
			else
				this.overrides.put(slot, mod);
		}
		return ext;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return this.delegate.getSlotLimit(slot);
	}
}
