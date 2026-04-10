package dev.enginecrafter77.imhotepmc.util.transaction;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemStackTransaction implements Transaction {
	private final List<SlotAssignment> consumeSlots;
	private final List<SlotAssignment> recoverSlots;

	@Nullable
	private IItemHandler source;
	@Nullable
	private IItemHandler destination;

	private int mismatched;
	private boolean valid;

	public ItemStackTransaction(ItemStackTransactionTemplate template)
	{
		this.consumeSlots = template.getConsumedItems().stream().map(SlotAssignment::new).collect(Collectors.toList());
		this.recoverSlots = template.getRecoveredItems().stream().map(SlotAssignment::new).collect(Collectors.toList());
		this.valid = false;
	}

	public void setSource(@Nullable IItemHandler source)
	{
		this.source = source;
		this.invalidate();
	}

	public void setDestination(@Nullable IItemHandler destination)
	{
		this.destination = destination;
		this.invalidate();
	}

	public Stream<ItemStack> mismatchedConsumes()
	{
		return this.consumeSlots.stream().filter(SlotAssignment::isMissing).map(SlotAssignment::getStack);
	}

	public Stream<ItemStack> mismatchedRecovers()
	{
		return this.consumeSlots.stream().filter(SlotAssignment::isMissing).map(SlotAssignment::getStack);
	}

	private void matchSlots()
	{
		if(this.source == null || this.destination == null)
			return;
		CowInvView srcView = new CowInvView(this.source);
		CowInvView dstView = this.source == this.destination ? srcView : new CowInvView(this.destination);

		for(SlotAssignment assignment : this.consumeSlots)
		{
			assignment.setSlot(-1);
			for(int i = 0; i < srcView.getSlots(); ++i)
			{
				ItemStack contents = srcView.getStackInSlot(i);
				if(!contents.isItemEqual(assignment.getStack()))
					continue;
				ItemStack extracted = srcView.extractItem(i, assignment.getStack().getCount(), false);
				if(extracted.getCount() == assignment.getStack().getCount())
				{
					assignment.setSlot(i);
					break;
				}
			}
		}

		for(SlotAssignment assignment : this.recoverSlots)
		{
			assignment.setSlot(-1);
			for(int i = 0; i < dstView.getSlots(); ++i)
			{
				ItemStack inserted = dstView.insertItem(i, assignment.getStack(), false);
				if(inserted.isEmpty())
				{
					assignment.setSlot(i);
					break;
				}
			}
		}
		this.mismatched = (int)(this.mismatchedConsumes().count() + this.mismatchedRecovers().count());
	}

	private void update()
	{
		if(this.valid)
			return;
		this.matchSlots();
		this.valid = true;
	}

	public void invalidate()
	{
		this.valid = false;
	}

	@Override
	public boolean canCommit()
	{
		this.update();
		return this.source != null && this.destination != null && this.mismatched == 0;
	}

	@Override
	public void commit()
	{
		assert this.source != null;
		assert this.destination != null;
		for(SlotAssignment ca : this.consumeSlots)
			ca.consumeFrom(this.source);
		for(SlotAssignment ra : this.recoverSlots)
			ra.insertInto(this.destination);
	}

	public static class SlotAssignment
	{
		private final ItemStack stack;
		private int slot;

		public SlotAssignment(ItemStack stack)
		{
			this.stack = stack;
			this.slot = -1;
		}

		public void setSlot(int slot)
		{
			this.slot = slot;
		}

		public ItemStack getStack()
		{
			return this.stack;
		}

		public boolean isMissing()
		{
			return this.slot == -1;
		}

		public ItemStack consumeFrom(IItemHandler handler)
		{
			return handler.extractItem(this.slot, this.stack.getCount(), false);
		}

		public ItemStack insertInto(IItemHandler handler)
		{
			return handler.insertItem(this.slot, this.stack, false);
		}
	}
}
