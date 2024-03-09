package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;

import javax.vecmath.Tuple3i;

public class Vector3i extends Tuple3i implements INBTSerializable<NBTTagCompound> {
	public Vector3i()
	{
		super();
	}

	public Vector3i(int x, int y, int z)
	{
		super(x, y, z);
	}

	public Vector3i(Vec3i src)
	{
		this(src.getX(), src.getY(), src.getZ());
	}

	public Vector3i(NBTTagCompound tag)
	{
		this();
		this.deserializeNBT(tag);
	}

	public Vec3i toVec3i()
	{
		return new Vec3i(this.x, this.y, this.z);
	}

	public BlockPos toBlockPos()
	{
		return new BlockPos(this.x, this.y, this.z);
	}

	public void toBlockPos(BlockPos.MutableBlockPos blockPos)
	{
		blockPos.setPos(this.x, this.y, this.z);
	}

	public void serializeNBTInto(NBTTagCompound tag)
	{
		tag.setInteger("x", this.x);
		tag.setInteger("y", this.y);
		tag.setInteger("z", this.z);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.serializeNBTInto(tag);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.x = nbt.getInteger("x");
		this.y = nbt.getInteger("y");
		this.z = nbt.getInteger("z");
	}
}
