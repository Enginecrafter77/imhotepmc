package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBlueprintLibrary extends TileEntity {
	public static final ResourceLocation ID = new ResourceLocation(ImhotepMod.MOD_ID, "blueprint_library");

	private static final String INVENTORY = "inventory";

	private final ItemStackHandler inventory;

	public TileEntityBlueprintLibrary()
	{
		this.inventory = new ItemStackHandler();
		this.inventory.setSize(1);
	}

	@Nonnull
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.pos, 0, this.serializeNBT());
	}

	@Override
	public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt)
	{
		super.onDataPacket(net, pkt);
		this.deserializeNBT(pkt.getNbtCompound());
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.inventory.deserializeNBT(compound.getCompoundTag(INVENTORY));
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag(INVENTORY, this.inventory.serializeNBT());
		return compound;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory);
		return super.getCapability(capability, facing);
	}
}
