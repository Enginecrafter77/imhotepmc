package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.blueprint.builder.BuilderMaterialStorage;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
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
	public boolean hasBlock(Block block)
	{
		return this.findSlotWithBlock(block) != -1;
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return this.findSlotForItemStack(stack) != -1;
	}

	@Override
	public ItemStack consumeBlock(Block block)
	{
		int slot = this.findSlotWithBlock(block);
		if(slot == -1)
			return ItemStack.EMPTY;
		return this.storage.extractItem(slot, 1, false);
	}

	@Override
	public void addBlockDrops(ItemStack drop)
	{
		int slot = this.findSlotForItemStack(drop);
		if(slot == -1)
			return;
		this.storage.insertItem(slot, drop, false);
	}

	private int findSlotWithBlock(Block block)
	{
		ItemStack stack = new ItemStack(Item.getItemFromBlock(block));
		return this.findSlot((ItemStack stackInSlot) -> ItemStack.areItemsEqual(stack, stackInSlot));
	}

	private int findSlotForItemStack(ItemStack stack)
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
