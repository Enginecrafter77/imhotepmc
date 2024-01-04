package dev.enginecrafter77.imhotepmc.container;

import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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

		PlayerInventoryGenerator.from(inventoryPlayer).generate(this, 7, 100);
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn)
	{
		return true;
	}
}
