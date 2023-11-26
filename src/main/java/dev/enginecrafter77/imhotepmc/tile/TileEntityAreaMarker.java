package dev.enginecrafter77.imhotepmc.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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

	public boolean tryConnect(TileEntityAreaMarker other)
	{
		AreaMarkGroup ng = this.group.merge(other.getCurrentMarkGroup());
		if(ng == null)
			return false;

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
