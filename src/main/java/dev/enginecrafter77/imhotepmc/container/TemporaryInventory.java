package dev.enginecrafter77.imhotepmc.container;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Predicate;

public class TemporaryInventory implements IInventory {
	@Nullable
	private final String name;
	private final ItemStack[] stacks;
	private final int[] fields;

	public TemporaryInventory(int size, @Nullable String name)
	{
		this.name = name;
		this.fields = new int[size];
		this.stacks = new ItemStack[size];
		this.clear();
	}

	public TemporaryInventory(int size)
	{
		this(size, null);
	}

	@Override
	public int getSizeInventory()
	{
		return this.stacks.length;
	}

	@Override
	public boolean isEmpty()
	{
		return Arrays.stream(this.stacks).allMatch(Predicate.isEqual(ItemStack.EMPTY));
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return this.stacks[index];
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		ItemStack rem = this.stacks[index].splitStack(count);
		if(this.stacks[index].getCount() == 0)
			this.stacks[index] = ItemStack.EMPTY;
		return rem;
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack stack = this.stacks[index];
		this.stacks[index] = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack)
	{
		this.stacks[index] = stack;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(@Nonnull EntityPlayer player) {}

	@Override
	public void closeInventory(@Nonnull EntityPlayer player)
	{
		for(ItemStack stack : this.stacks)
		{
			if(stack == ItemStack.EMPTY)
				continue;
			boolean added = player.inventory.addItemStackToInventory(stack);
			if(added)
				continue;

			EntityItem item = new EntityItem(player.world);
			item.setItem(stack);
			item.setPosition(player.posX, player.posY, player.posZ);
			player.world.spawnEntity(item);
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack)
	{
		return true;
	}

	@Override
	public int getField(int id)
	{
		return this.fields[id];
	}

	@Override
	public void setField(int id, int value)
	{
		this.fields[id] = value;
	}

	@Override
	public int getFieldCount()
	{
		return this.fields.length;
	}

	@Override
	public void clear()
	{
		Arrays.fill(this.stacks, ItemStack.EMPTY);
	}

	@Nonnull
	@Override
	public String getName()
	{
		if(this.name == null)
			return "<unnamed>";
		return this.name;
	}

	@Override
	public boolean hasCustomName()
	{
		return this.name != null;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName()
	{
		return new TextComponentString(this.getName());
	}
}
