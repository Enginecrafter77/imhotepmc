package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.world.AreaMarkDatabase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityAreaMarker extends TileEntity implements IAreaMarker {
	private static final String NBT_KEY_GROUP = "group";

	@Nullable
	private UUID areaMarkGroupId;

	public TileEntityAreaMarker()
	{
		this.areaMarkGroupId = null;
	}

	public boolean tryConnect(TileEntityAreaMarker other, EntityPlayer actor)
	{
		if(this.areaMarkGroupId != null && other.areaMarkGroupId != null)
			return false;

		if(this.areaMarkGroupId == null && other.areaMarkGroupId != null)
			return other.tryConnect(this, actor);

		int stored = 0;
		AreaMarkGroup ng;
		AreaMarkGroup grp = this.getCurrentMarkGroup();
		if(grp == null)
		{
			ng = AreaMarkGroup.create(this.getPos(), other.getPos());
		}
		else
		{
			stored = grp.getUsedTapeCount();
			ng = grp.expand(other.getPos());
		}

		if(ng == null)
			return false;

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

		if(grp != null)
			grp.dismantle(this.world);
		ng.construct(this.world);
		return true;
	}

	@Override
	public BlockPos getMarkerPosition()
	{
		return this.getPos();
	}

	@Nullable
	@Override
	public AreaMarkGroup getCurrentMarkGroup()
	{
		if(this.areaMarkGroupId == null)
			return null;

		AreaMarkDatabase db = AreaMarkDatabase.getDefault(this.world);
		if(db == null)
			return null;
		return db.getGroup(this.areaMarkGroupId);
	}

	@Override
	public void setMarkGroup(@Nullable AreaMarkGroup group)
	{
		if(group == null)
		{
			this.areaMarkGroupId = null;
			return;
		}
		this.areaMarkGroupId = group.getId();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if(compound.hasKey(NBT_KEY_GROUP))
			this.areaMarkGroupId = NBTUtil.getUUIDFromTag(compound.getCompoundTag(NBT_KEY_GROUP));
		else
			this.areaMarkGroupId = null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		if(this.areaMarkGroupId != null)
			compound.setTag(NBT_KEY_GROUP, NBTUtil.createUUIDTag(this.areaMarkGroupId));
		return compound;
	}
}
