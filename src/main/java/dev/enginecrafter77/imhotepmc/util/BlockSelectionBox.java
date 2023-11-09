package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class BlockSelectionBox implements INBTSerializable<NBTTagCompound> {
	private static final String KEY_NBT_SOURCE_X = "s_x";
	private static final String KEY_NBT_SOURCE_Y = "s_y";
	private static final String KEY_NBT_SOURCE_Z = "s_z";
	private static final String KEY_NBT_DEST_X = "d_x";
	private static final String KEY_NBT_DEST_Y = "d_y";
	private static final String KEY_NBT_DEST_Z = "d_z";

	private final BlockPos.MutableBlockPos start;
	private final BlockPos.MutableBlockPos end;

	public BlockSelectionBox()
	{
		this(BlockPos.ORIGIN, BlockPos.ORIGIN);
	}

	public BlockSelectionBox(@Nonnull BlockPos start, @Nonnull BlockPos end)
	{
		this.start = new BlockPos.MutableBlockPos(start);
		this.end = new BlockPos.MutableBlockPos(end);
	}

	public void setStart(@Nonnull BlockPos start)
	{
		this.start.setPos(start);
	}

	public void setEnd(@Nonnull BlockPos end)
	{
		this.end.setPos(end);
	}

	public void set(BlockSelectionBox other)
	{
		this.start.setPos(other.start);
		this.end.setPos(other.end);
	}

	public void reset()
	{
		this.start.setPos(BlockPos.ORIGIN);
		this.end.setPos(BlockPos.ORIGIN);
	}

	public Vec3i getSize()
	{
		int sx = Math.abs(this.end.getX() - this.start.getX());
		int sy = Math.abs(this.end.getY() - this.start.getY());
		int sz = Math.abs(this.end.getZ() - this.start.getZ());
		return new Vec3i(sx, sy, sz);
	}

	public Iterable<BlockPos.MutableBlockPos> volume()
	{
		return BlockPos.getAllInBoxMutable(this.start, this.end);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger(KEY_NBT_SOURCE_X, this.start.getX());
		compound.setInteger(KEY_NBT_SOURCE_Y, this.start.getY());
		compound.setInteger(KEY_NBT_SOURCE_Z, this.start.getZ());
		compound.setInteger(KEY_NBT_DEST_X, this.end.getX());
		compound.setInteger(KEY_NBT_DEST_Y, this.end.getY());
		compound.setInteger(KEY_NBT_DEST_Z, this.end.getZ());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		int sx = nbt.getInteger(KEY_NBT_SOURCE_X);
		int sy = nbt.getInteger(KEY_NBT_SOURCE_Y);
		int sz = nbt.getInteger(KEY_NBT_SOURCE_Z);
		int dx = nbt.getInteger(KEY_NBT_DEST_X);
		int dy = nbt.getInteger(KEY_NBT_DEST_Y);
		int dz = nbt.getInteger(KEY_NBT_DEST_Z);

		this.start.setPos(sx, sy, sz);
		this.end.setPos(dx, dy, dz);
	}
}
