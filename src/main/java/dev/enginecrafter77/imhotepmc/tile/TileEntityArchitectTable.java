package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class TileEntityArchitectTable extends TileEntity {
	private static final String NBT_KEY_SELECTION = "selection";

	private final BlockSelectionBox selection;

	public TileEntityArchitectTable()
	{
		this.selection = new BlockSelectionBox();
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.selection.deserializeNBT(compound.getCompoundTag(NBT_KEY_SELECTION));
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag(NBT_KEY_SELECTION, this.selection.serializeNBT());
		return compound;
	}
}
