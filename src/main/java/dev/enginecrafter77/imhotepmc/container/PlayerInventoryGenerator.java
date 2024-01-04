package dev.enginecrafter77.imhotepmc.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class PlayerInventoryGenerator implements SlotGenerator {
	private final InventoryPlayer player;

	private final GridSlotGenerator inventory;
	private final GridSlotGenerator hotbar;

	public PlayerInventoryGenerator(InventoryPlayer player)
	{
		this.player = player;
		this.hotbar = new GridSlotGenerator(this::createSlot, GridSlotGenerator.SlotIndexer.horizontal(0), 1, 9);
		this.inventory = new GridSlotGenerator(this::createSlot, GridSlotGenerator.SlotIndexer.horizontal(9), 3, 9);
	}

	protected Slot createSlot(int index, int x, int y)
	{
		return new Slot(this.player, index, x, y);
	}

	@Override
	public void generate(Container container, int x, int y)
	{
		this.inventory.generate(container, x, y);
		this.hotbar.generate(container, x, y + 58);
	}

	public static PlayerInventoryGenerator from(InventoryPlayer player)
	{
		return new PlayerInventoryGenerator(player);
	}
}
