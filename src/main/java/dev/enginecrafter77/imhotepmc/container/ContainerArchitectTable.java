package dev.enginecrafter77.imhotepmc.container;

import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ContainerArchitectTable extends Container {
	public ContainerArchitectTable(InventoryPlayer inventoryPlayer, TileEntityArchitectTable tile)
	{
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if(handler == null)
			throw new IllegalStateException();

		this.addSlotToContainer(new SlotBlueprint(handler, 0, 133, 21));

		for(int row = 0; row < 3; ++row)
		{
			for(int col = 0; col < 9; ++col)
			{
				int slot = (row + 1) * 9 + col;
				int xpos = 7 + col * 18;
				int ypos = 100 + row * 18;
				this.addSlotToContainer(new Slot(inventoryPlayer, slot, xpos, ypos));
			}
		}

		for(int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot)
		{
			int xpos = 7 + hotbarSlot * 18;
			this.addSlotToContainer(new Slot(inventoryPlayer, hotbarSlot, xpos, 158));
		}
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn)
	{
		return true;
	}
}
