package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityAreaMarker extends TileEntity implements IAreaMarker {
	private static final String NBT_KEY_LINK = "link";

	@Nonnull
	private AreaMarkGroup group;

	public TileEntityAreaMarker()
	{
		this.group = AreaMarkGroup.voxel(BlockPos.ORIGIN);
	}

	public boolean tryConnect(TileEntityAreaMarker other, EntityPlayer actor)
	{
		AreaMarkGroup ng = this.group.merge(other.getCurrentMarkGroup());
		if(ng == null)
			return false;

		int stored = this.group.getUsedTapeCount() + other.group.getUsedTapeCount();
		int required = ng.getUsedTapeCount();
		int consume = required - stored;
		ItemStack stack = new ItemStack(ImhotepMod.ITEM_CONSTRUCTION_TAPE, Math.abs(consume));

		if(consume < 0)
		{
			EntityItem item = new EntityItem(this.world);
			item.setItem(stack);
			Vec3d center = new Vec3d(this.getPos()).add(0.5D, 0.5D, 0.5D);
			item.setPosition(center.x, center.y, center.z);
			this.world.spawnEntity(item);
		}
		else if(!actor.isCreative())
		{
			int slot = actor.inventory.findSlotMatchingUnusedItem(stack);
			if(slot == -1)
				return false;
			ItemStack stackInSlot = actor.inventory.getStackInSlot(slot);
			if(stackInSlot.getCount() < consume)
				return false;
			actor.inventory.decrStackSize(slot, consume);
		}

		this.group.dismantle(this.world, TileEntityAreaMarker::getMarkerFromTile);
		other.group.dismantle(this.world, TileEntityAreaMarker::getMarkerFromTile);
		ng.construct(this.world, TileEntityAreaMarker::getMarkerFromTile);
		return true;
	}

	@Override
	public BlockPos getMarkerPosition()
	{
		return this.getPos();
	}

	@Override
	public AreaMarkGroup getCurrentMarkGroup()
	{
		return this.group;
	}

	@Override
	public void setMarkGroup(AreaMarkGroup group)
	{
		this.group = group;
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		if(this.group.getDefined() == 1)
			this.group = AreaMarkGroup.voxel(this.getPos());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.group.deserializeNBT(compound.getCompoundTag(NBT_KEY_LINK));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag(NBT_KEY_LINK, this.group.serializeNBT());
		return compound;
	}

	@Nullable
	public static IAreaMarker getMarkerFromTile(IBlockAccess world, BlockPos pos)
	{
		TileEntity tile = (TileEntity)world.getTileEntity(pos);
		if(!(tile instanceof IAreaMarker))
			return null;
		return (IAreaMarker)tile;
	}
}
