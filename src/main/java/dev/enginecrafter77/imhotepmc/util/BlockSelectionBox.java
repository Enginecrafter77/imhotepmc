package dev.enginecrafter77.imhotepmc.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockSelectionBox extends BlockPosBox implements INBTSerializable<NBTTagCompound> {
	private static final String KEY_NBT_SOURCE_X = "x1";
	private static final String KEY_NBT_SOURCE_Y = "y1";
	private static final String KEY_NBT_SOURCE_Z = "z1";
	private static final String KEY_NBT_DEST_X = "x2";
	private static final String KEY_NBT_DEST_Y = "y2";
	private static final String KEY_NBT_DEST_Z = "z2";

	private final BlockPos.MutableBlockPos start; // Inclusive
	private final BlockPos.MutableBlockPos end; // Inclusive

	public BlockSelectionBox()
	{
		this.start = new BlockPos.MutableBlockPos(BlockPos.ORIGIN);
		this.end = new BlockPos.MutableBlockPos(BlockPos.ORIGIN);
	}

	public void setStartEnd(@Nonnull BlockPos start, @Nonnull BlockPos end)
	{
		this.setToContain(ImmutableList.of(start, end));
	}

	public void setStartSize(@Nonnull BlockPos start, @Nonnull Vec3i size)
	{
		int stx = start.getX();
		int sty = start.getY();
		int stz = start.getZ();

		int vx = size.getX();
		int vy = size.getY();
		int vz = size.getZ();

		if(vx < 0)
		{
			stx += vx + 1;
			vx = Math.abs(vx);
		}

		if(vy < 0)
		{
			sty += vy + 1;
			vy = Math.abs(vy);
		}

		if(vz < 0)
		{
			stz += vz + 1;
			vz = Math.abs(vz);
		}

		this.start.setPos(stx, sty, stz);
		this.end.setPos(stx + vx - 1, sty + vy - 1, stz + vz - 1);
	}

	@Override
	public BlockPos getMinCorner()
	{
		return this.start;
	}

	@Override
	public Vec3i getSize()
	{
		int sx = Math.max(this.end.getX() - this.start.getX() + 1, 0);
		int sy = Math.max(this.end.getY() - this.start.getY() + 1, 0);
		int sz = Math.max(this.end.getZ() - this.start.getZ() + 1, 0);
		return new Vec3i(sx, sy, sz);
	}

	@Override
	public BlockPos getMaxCorner()
	{
		return this.end;
	}

	public void set(BlockPosBox other)
	{
		this.start.setPos(other.getMinCorner());
		this.end.setPos(other.getMaxCorner());
	}

	public void intersect(BlockPosBox other)
	{
		BlockPos minOther = other.getMinCorner();
		BlockPos maxOther = other.getMaxCorner();

		int dmx = Math.max(this.start.getX(), minOther.getX());
		int dmy = Math.max(this.start.getY(), minOther.getY());
		int dmz = Math.max(this.start.getZ(), minOther.getZ());
		int dMx = Math.min(this.end.getX(), maxOther.getX());
		int dMy = Math.min(this.end.getY(), maxOther.getY());
		int dMz = Math.min(this.end.getZ(), maxOther.getZ());

		this.start.setPos(dmx, dmy, dmz);
		this.end.setPos(dMx, dMy, dMz);
	}

	public void union(BlockPosBox other)
	{
		BlockPos minOther = other.getMinCorner();
		BlockPos maxOther = other.getMaxCorner();

		int dmx = Math.min(this.start.getX(), minOther.getX());
		int dmy = Math.min(this.start.getY(), minOther.getY());
		int dmz = Math.min(this.start.getZ(), minOther.getZ());
		int dMx = Math.max(this.end.getX(), maxOther.getX());
		int dMy = Math.max(this.end.getY(), maxOther.getY());
		int dMz = Math.max(this.end.getZ(), maxOther.getZ());

		this.start.setPos(dmx, dmy, dmz);
		this.end.setPos(dMx, dMy, dMz);
	}

	public Iterable<BlockPos.MutableBlockPos> internalBlocks()
	{
		return BlockPos.getAllInBoxMutable(this.start, this.end);
	}

	public void setToContain(Iterable<BlockPos> itr)
	{
		boolean empty = true;
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;
		int x, y, z;
		for(BlockPos pos : itr)
		{
			x = pos.getX();
			y = pos.getY();
			z = pos.getZ();
			if(x < minX)
				minX = x;
			if(y < minY)
				minY = y;
			if(z < minZ)
				minZ = z;
			if(x > maxX)
				maxX = x;
			if(y > maxY)
				maxY = y;
			if(z > maxZ)
				maxZ = z;
			empty = false;
		}

		if(empty)
			return;

		this.start.setPos(minX, minY, minZ);
		this.end.setPos(maxX, maxY, maxZ);
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
