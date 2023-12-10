package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.blueprint.builder.BuilderMaterialStorage;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public class InventoryMaterialStorage implements BuilderMaterialStorage {
	private final IItemHandler storage;

	public InventoryMaterialStorage(IItemHandler storage)
	{
		this.storage = storage;
	}

	@Nullable
	@Override
	public Block getAnyAvailableBlock()
	{
		int slots = this.storage.getSlots();
		for(int slot = 0; slot < slots; ++slot)
		{
			ItemStack stackInSlot = this.storage.getStackInSlot(slot);
			if(stackInSlot.getItem() instanceof ItemBlock)
			{
				ItemBlock blk = (ItemBlock)stackInSlot.getItem();
				return blk.getBlock();
			}
		}
		return null;
	}

	@Override
	public boolean canProvide(Collection<ItemStack> req)
	{
		for(ItemStack stack : req)
		{
			int slot = this.findSlotForExtract(stack);
			if(slot == -1)
				return false;
		}
		return true;
	}

	@Override
	public boolean canReclaim(Collection<ItemStack> req)
	{
		for(ItemStack stack : req)
		{
			int slot = this.findSlotForInsert(stack);
			if(slot == -1)
				return false;
		}
		return true;
	}

	@Override
	public void provide(Collection<ItemStack> req)
	{
		for(ItemStack stack : req)
		{
			int slot = this.findSlotForExtract(stack);
			if(slot == -1)
				continue;
			this.storage.extractItem(slot, stack.getCount(), false);
		}
	}

	@Override
	public void reclaim(Collection<ItemStack> req)
	{
		for(ItemStack stack : req)
		{
			int slot = this.findSlotForInsert(stack);
			if(slot == -1)
				continue;
			this.storage.insertItem(slot, stack, false);
		}
	}

	private int findSlotForExtract(ItemStack stack)
	{
		return this.findSlot((ItemStack stackInSlot) -> ItemStack.areItemsEqual(stack, stackInSlot));
	}

	private int findSlotForInsert(ItemStack stack)
	{
		return this.findSlot((ItemStack stackInSlot) -> stackInSlot.isEmpty() || (ItemStack.areItemsEqual(stack, stackInSlot) && (stackInSlot.getCount() + stack.getCount()) <= stackInSlot.getItem().getItemStackLimit(stackInSlot)));
	}

	private int findSlot(Predicate<ItemStack> slotMatcher)
	{
		int slots = this.storage.getSlots();
		for(int slot = 0; slot < slots; ++slot)
		{
			ItemStack stackInSlot = this.storage.getStackInSlot(slot);
			if(slotMatcher.test(stackInSlot))
				return slot;
		}
		return -1;
	}
}
