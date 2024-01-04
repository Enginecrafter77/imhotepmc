package dev.enginecrafter77.imhotepmc.tile;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.Objects;

public class SharedBuilderState implements INBTSerializable<NBTTagCompound> {
	private static final String NBT_KEY_MI = "MissingItems";
	private static final String NBT_KEY_POWERED = "Powered";

	private Collection<ItemStack> missingItems;
	private boolean powered;

	public SharedBuilderState()
	{
		this.missingItems = ImmutableList.of();
		this.powered = false;
	}

	public void set(SharedBuilderState other)
	{
		this.missingItems = other.missingItems;
		this.powered = other.powered;
	}

	public void setMissingItems(Collection<ItemStack> missingItems)
	{
		this.missingItems = missingItems;
	}

	public Collection<ItemStack> getMissingItems()
	{
		return this.missingItems;
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
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		for(NBTBase missingItemBaseTag : nbt.getTagList(NBT_KEY_MI, 10))
			builder.add(new ItemStack((NBTTagCompound)missingItemBaseTag));
		this.missingItems = builder.build();
	}
}
