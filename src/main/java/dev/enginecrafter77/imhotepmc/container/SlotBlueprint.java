package dev.enginecrafter77.imhotepmc.container;

import dev.enginecrafter77.imhotepmc.item.ItemSchematicBlueprint;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotBlueprint extends SlotItemHandler {
	private final boolean requireWritten;

	public SlotBlueprint(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean requireWritten)
	{
		super(itemHandler, index, xPosition, yPosition);
		this.requireWritten = requireWritten;
	}

	public SlotBlueprint(IItemHandler itemHandler, int index, int xPosition, int yPosition)
	{
		this(itemHandler, index, xPosition, yPosition, false);
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		if(stack.getItem() != ItemSchematicBlueprint.INSTANCE)
			return false;
		return !this.requireWritten || ItemSchematicBlueprint.INSTANCE.getMetadata(stack) == ItemSchematicBlueprint.META_WRITTEN;
	}
}
