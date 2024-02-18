package dev.enginecrafter77.imhotepmc.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractItemStackTransaction implements ItemStackTransaction {
	private final Collection<ItemStack> transactionStacks;
	private final List<SlotAssignment> slots;

	private final List<ItemStack> blockingStacks;

	@Nullable
	private IItemHandler inventory;

	public AbstractItemStackTransaction(Collection<ItemStack> stacks)
	{
		this.transactionStacks = stacks;
		this.slots = stacks.stream().map(SlotAssignment::create).collect(Collectors.toList());
		this.blockingStacks = new ArrayList<ItemStack>();
		this.inventory = null;
	}

	protected abstract boolean isSlotSuitable(@Nonnull IItemHandler inventory, ItemStack stack, int slot);
	protected abstract void performSlotOperation(@Nonnull IItemHandler inventory, ItemStack stack, int slot);

	@Override
	public Collection<ItemStack> getTransactionStacks()
	{
		return this.transactionStacks;
	}

	@Nonnull
	@Override
	public Collection<ItemStack> getBlockingStacks()
	{
		return this.blockingStacks;
	}

	@Override
	public void evaluate(IItemHandler inventory)
	{
		this.inventory = inventory;

		this.blockingStacks.clear();
		for(SlotAssignment assignment : this.slots)
		{
			assignment.setAssignedSlot(this.findSlot(inventory, assignment.getStack()));
			if(!assignment.hasAssignedSlot())
				this.blockingStacks.add(assignment.getStack());
		}
	}

	@Override
	public boolean canCommit()
	{
		return this.inventory != null && this.blockingStacks.isEmpty();
	}

	@Override
	public void commit()
	{
		if(this.inventory == null)
			throw new IllegalStateException();

		for(SlotAssignment assignment : this.slots)
		{
			Integer assignedSlot = assignment.getAssignedSlot();
			if(assignedSlot == null)
				throw new IllegalStateException();
			this.performSlotOperation(this.inventory, assignment.getStack(), assignedSlot);
		}
	}

	@Nullable
	private Integer findSlot(IItemHandler inventory, ItemStack stack)
	{
		for(int slot = 0; slot < inventory.getSlots(); ++slot)
		{
			if(this.isSlotSuitable(inventory, stack, slot))
				return slot;
		}
		return null;
	}

	private static class SlotAssignment
	{
		private final ItemStack stack;

		@Nullable
		private Integer slot;

		public SlotAssignment(ItemStack stack)
		{
			this.stack = stack;
			this.slot = null;
		}

		public ItemStack getStack()
		{
			return this.stack;
		}

		@Nullable
		public Integer getAssignedSlot()
		{
			return this.slot;
		}

		public void setAssignedSlot(@Nullable Integer assignedSlot)
		{
			this.slot = assignedSlot;
		}

		public boolean hasAssignedSlot()
		{
			return this.slot != null;
		}

		public static SlotAssignment create(ItemStack stack)
		{
			return new SlotAssignment(stack);
		}
	}
}
