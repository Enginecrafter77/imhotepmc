package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;

public class Vec3dSerializer implements DataSerializer<Vec3d> {
	public static final Vec3dSerializer INSTANCE = new Vec3dSerializer();

	@Override
	public void write(PacketBuffer buf, Vec3d value)
	{
		buf.writeDouble(value.x);
		buf.writeDouble(value.y);
		buf.writeDouble(value.z);
	}

	@Override
	public Vec3d read(PacketBuffer buf) throws IOException
	{
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();
		return new Vec3d(x, y, z);
	}

	@Override
	public DataParameter<Vec3d> createKey(int id)
	{
		return new DataParameter<Vec3d>(id, this);
	}

	@Override
	public Vec3d copyValue(Vec3d value)
	{
		return value;
	}
}
