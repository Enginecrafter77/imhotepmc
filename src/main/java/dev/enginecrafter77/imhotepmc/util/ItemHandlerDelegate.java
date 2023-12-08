package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ItemHandlerDelegate implements IItemHandler {
	private final Supplier<IItemHandler> handlerSupplier;

	public ItemHandlerDelegate(Supplier<IItemHandler> handlerSupplier)
	{
		this.handlerSupplier = handlerSupplier;
	}

	@Override
	public int getSlots()
	{
		IItemHandler handler = this.handlerSupplier.get();
		if(handler == null)
			return 0;
		return handler.getSlots();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		IItemHandler handler = this.handlerSupplier.get();
		if(handler == null)
			return ItemStack.EMPTY;
		return handler.getStackInSlot(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		IItemHandler handler = this.handlerSupplier.get();
		if(handler == null)
			return stack;
		return handler.insertItem(slot, stack, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		IItemHandler handler = this.handlerSupplier.get();
		if(handler == null)
			return ItemStack.EMPTY;
		return handler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot)
	{
		IItemHandler handler = this.handlerSupplier.get();
		if(handler == null)
			return 0;
		return handler.getSlotLimit(slot);
	}
}
