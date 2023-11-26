package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.Vec3d;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class Edge3d {
	private final Point3d p1;
	private final Point3d p2;

	public Edge3d()
	{
		this.p1 = new Point3d();
		this.p2 = new Point3d();
	}

	public void set(Vec3d v1, Vec3d v2)
	{
		this.set(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}

	public void set(Tuple3d p1, Tuple3d p2)
	{
		this.p1.set(p1);
		this.p2.set(p2);
	}

	public void set(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		this.p1.set(x1, y1, y1);
		this.p2.set(x2, y2, y2);
	}

	public void getFirstPoint(Tuple3d dest)
	{
		dest.set(this.p1);
	}

	public void getSecondPoint(Tuple3d dest)
	{
		dest.set(this.p2);
	}

	public Vec3d getFirstPointAsVec3d()
	{
		return new Vec3d(this.p1.x, this.p1.y, this.p1.z);
	}

	public Vec3d getSecondPointAsVec3d()
	{
		return new Vec3d(this.p2.x, this.p2.y, this.p2.z);
	}

	public double getLength()
	{
		return this.p2.distance(this.p1);
	}
}
