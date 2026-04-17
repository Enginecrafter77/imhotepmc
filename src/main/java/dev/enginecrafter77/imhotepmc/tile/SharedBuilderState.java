package dev.enginecrafter77.imhotepmc.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SharedBuilderState implements INBTSerializable<NBTTagCompound> {
	private static final String NBT_KEY_MI = "MissingItems";
	private static final String NBT_KEY_POWERED = "Powered";

	private List<ItemStack> missingItems;
	private boolean powered;

	public SharedBuilderState()
	{
		this.missingItems = new ArrayList<>();
		this.powered = false;
	}

	public void set(SharedBuilderState other)
	{
		this.missingItems = other.missingItems;
		this.powered = other.powered;
	}

	public void setMissingItems(List<ItemStack> missingItems)
	{
		this.missingItems.clear();
		this.missingItems.addAll(missingItems);
	}

	public void setMissingItemsFrom(Stream<ItemStack> missingItems)
	{
		this.missingItems.clear();
		missingItems.forEach(this.missingItems::add);
	}

	public void clearMissingItems(Stream<ItemStack> missingItems)
	{
		this.missingItems.clear();
		missingItems.forEach(this.missingItems::add);
	}

	public List<ItemStack> getMissingItems()
	{
		return Collections.unmodifiableList(this.missingItems);
	}

	public boolean hasPower()
	{
		return this.powered;
	}

	public void setPowered(boolean powered)
	{
		this.powered = powered;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;
		if(!(obj instanceof SharedBuilderState))
			return false;
		SharedBuilderState other = (SharedBuilderState)obj;
		return Objects.equals(this.missingItems, other.missingItems) && Objects.equals(this.powered, other.powered);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.missingItems, this.powered);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList missingItemsTag = new NBTTagList();
		for(ItemStack missing : this.missingItems)
			missingItemsTag.appendTag(missing.serializeNBT());
		tag.setTag(NBT_KEY_MI, missingItemsTag);
		tag.setBoolean(NBT_KEY_POWERED, this.powered);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.powered = nbt.getBoolean(NBT_KEY_POWERED);

		this.missingItems.clear();
		for(NBTBase missingItemBaseTag : nbt.getTagList(NBT_KEY_MI, 10))
			this.missingItems.add(new ItemStack((NBTTagCompound)missingItemBaseTag));
	}

	@Override
	public String toString()
	{
		return String.format("SharedBuilderState(P:%b,MI:%s)", this.powered, this.missingItems);
	}
}
