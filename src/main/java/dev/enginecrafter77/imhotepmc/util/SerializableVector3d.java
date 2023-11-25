package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;

import javax.vecmath.Vector3d;

public class SerializableVector3d extends Vector3d implements INBTSerializable<NBTTagCompound> {
	public SerializableVector3d(double x, double y, double z)
	{
		super(x, y, z);
	}

	public SerializableVector3d(Vector3d from)
	{
		super(from);
	}

	public SerializableVector3d(Vec3d from)
	{
		this(from.x, from.y, from.z);
	}

	public SerializableVector3d(Vec3i from)
	{
		this(from.getX(), from.getY(), from.getZ());
	}

	public SerializableVector3d()
	{
		super();
	}

	public void set(Vec3i vec)
	{
		this.set(vec.getX(), vec.getY(), vec.getZ());
	}

	public void set(Vec3d vec)
	{
		this.set(vec.x, vec.y, vec.z);
	}

	public Vec3d toVec3d()
	{
		return new Vec3d(this.x, this.y, this.z);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble("x", this.x);
		tag.setDouble("y", this.y);
		tag.setDouble("z", this.z);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.x = nbt.getDouble("x");
		this.y = nbt.getDouble("y");
		this.z = nbt.getDouble("z");
	}
}
