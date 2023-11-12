package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockSelectionBox implements INBTSerializable<NBTTagCompound> {
	private static final String KEY_NBT_SOURCE_X = "x1";
	private static final String KEY_NBT_SOURCE_Y = "y1";
	private static final String KEY_NBT_SOURCE_Z = "z1";
	private static final String KEY_NBT_DEST_X = "x2";
	private static final String KEY_NBT_DEST_Y = "y2";
	private static final String KEY_NBT_DEST_Z = "z2";

	private static final Vec3i ONE = new Vec3i(1, 1, 1);

	private final BlockPos.MutableBlockPos start; // Inclusive
	private final BlockPos.MutableBlockPos end; // Exclusive

	public BlockSelectionBox()
	{
		this.start = new BlockPos.MutableBlockPos(BlockPos.ORIGIN);
		this.end = new BlockPos.MutableBlockPos(BlockPos.ORIGIN);
	}

	public BlockSelectionBox(@Nonnull BlockPos start, @Nonnull BlockPos end)
	{
		this();
		this.setStart(start);
		this.setEnd(end);
	}

	public void setStart(@Nonnull BlockPos start)
	{
		this.start.setPos(start);
	}

	public void setEnd(@Nonnull BlockPos end)
	{
		this.end.setPos(end.add(ONE));
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

	public boolean contains(Vec3i vector)
	{
		return vector.getX() >= this.start.getX() && vector.getX() < this.end.getX() &&
				vector.getY() >= this.start.getY() && vector.getY() < this.end.getY() &&
				vector.getZ() >= this.start.getZ() && vector.getZ() < this.end.getZ();
	}

	public void intersect(BlockSelectionBox other)
	{
		int dmx = Math.max(this.start.getX(), other.start.getX());
		int dmy = Math.max(this.start.getY(), other.start.getY());
		int dmz = Math.max(this.start.getZ(), other.start.getZ());
		int dMx = Math.min(this.end.getX(), other.end.getX());
		int dMy = Math.min(this.end.getY(), other.end.getY());
		int dMz = Math.min(this.end.getZ(), other.end.getZ());

		if(dMx < dmx || dMy < dmy || dMz < dmz) // Non-overlapping
		{
			this.reset();
			return;
		}

		this.start.setPos(dmx, dmy, dmz);
		this.end.setPos(dMx, dMy, dMz);
	}

	public void union(BlockSelectionBox other)
	{
		int dmx = Math.min(this.start.getX(), other.start.getX());
		int dmy = Math.min(this.start.getY(), other.start.getY());
		int dmz = Math.min(this.start.getZ(), other.start.getZ());
		int dMx = Math.max(this.end.getX(), other.end.getX());
		int dMy = Math.max(this.end.getY(), other.end.getY());
		int dMz = Math.max(this.end.getZ(), other.end.getZ());
		this.start.setPos(dmx, dmy, dmz);
		this.end.setPos(dMx, dMy, dMz);
	}

	public int getVolume()
	{
		Vec3i size = this.getSize();
		return size.getX() * size.getY() * size.getZ();
	}

	public Iterable<BlockPos.MutableBlockPos> internalBlocks()
	{
		return BlockPos.getAllInBoxMutable(this.start, this.end);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof BlockSelectionBox))
			return false;
		BlockSelectionBox other = (BlockSelectionBox)obj;
		if(this == other)
			return true;

		return Objects.equals(this.start, other.start) && Objects.equals(this.end, other.end);
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

	@Override
	public String toString()
	{
		return String.format("Box(%d:%d:%d/%d:%d:%d)", this.start.getX(), this.start.getY(), this.start.getZ(), this.end.getX(), this.end.getY(), this.end.getZ());
	}
}
