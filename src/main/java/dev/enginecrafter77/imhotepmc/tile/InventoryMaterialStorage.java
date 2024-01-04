package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.blueprint.builder.BuilderMaterialStorage;
import dev.enginecrafter77.imhotepmc.blueprint.builder.ItemStackConsumeTransaction;
import dev.enginecrafter77.imhotepmc.blueprint.builder.ItemStackReclaimTransaction;
import dev.enginecrafter77.imhotepmc.blueprint.builder.ItemStackTransaction;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Collection;

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
	public ItemStackTransaction consume(Collection<ItemStack> items)
	{
		return new ItemStackConsumeTransaction(this.storage, items);
	}

	@Override
	public ItemStackTransaction reclaim(Collection<ItemStack> items)
	{
		return new ItemStackReclaimTransaction(this.storage, items);
	}
}
