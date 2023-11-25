package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.AxisAlignedBB;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public class Box3d {
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private double minZ;
	private double maxZ;

	public Box3d()
	{
		this.minX = 0D;
		this.minY = 0D;
		this.minZ = 0D;
		this.maxX = 0D;
		this.maxY = 0D;
		this.maxZ = 0D;
	}

	public Box3d(Vector3d start, Vector3d end)
	{
		this();
		this.set(start, end);
	}

	public Box3d(Box3d other)
	{
		this();
		this.set(other);
	}

	public void set(Box3d other)
	{
		this.minX = other.minX;
		this.minY = other.minY;
		this.minZ = other.minZ;
		this.maxX = other.maxX;
		this.maxY = other.maxY;
		this.maxZ = other.maxZ;
	}

	public void set(Vector3d start, Vector3d end)
	{
		this.minX = Math.min(start.x, end.x);
		this.minY = Math.min(start.y, end.y);
		this.minZ = Math.min(start.z, end.z);
		this.maxX = Math.max(start.x, end.x);
		this.maxY = Math.max(start.y, end.y);
		this.maxZ = Math.max(start.z, end.z);
	}

	public void grow(Tuple3d dim)
	{
		this.grow(dim.x, dim.y, dim.z);
	}

	public void grow(double x, double y, double z)
	{
		this.minX -= x;
		this.minY -= y;
		this.minZ -= z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
	}

	public void scale(Tuple3d sc)
	{
		this.scale(sc.x, sc.y, sc.z);
	}

	public void scale(double x, double y, double z)
	{
		double hlx = (this.maxX - this.minX) / 2D; // half x length
		double hly = (this.maxY - this.minY) / 2D; // half y length
		double hlz = (this.maxZ - this.minZ) / 2D; // half z length

		double mx = this.minX + hlx;
		double my = this.minY + hly;
		double mz = this.minZ + hlz;

		hlx *= x;
		hly *= y;
		hlz *= z;

		this.minX = mx - hlx;
		this.maxX = mx + hlx;
		this.minY = my - hly;
		this.maxY = my + hly;
		this.minZ = mz - hlz;
		this.maxZ = mz + hlz;
	}

	public void translate(double x, double y, double z)
	{
		this.minX += x;
		this.minY += y;
		this.minZ += z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
	}

	public void translateTo(double x, double y, double z)
	{
		double dx = x - this.minX;
		double dy = y - this.minY;
		double dz = z - this.minZ;
		this.translate(dx, dy, dz);
	}

	public void getStart(Vector3d dest)
	{
		dest.set(this.minX, this.minY, this.minZ);
	}

	public void getEnd(Vector3d dest)
	{
		dest.set(this.maxX, this.maxY, this.maxZ);
	}

	public void getCenter(Vector3d dest)
	{
		dest.set(this.minX + this.maxX, this.minY + this.maxY, this.minZ + this.maxZ);
		dest.scale(0.5D);
	}

	public void getSize(Tuple3d dest)
	{
		dest.x = this.maxX - this.minX;
		dest.y = this.maxY - this.minY;
		dest.z = this.maxZ - this.minZ;
	}

	public AxisAlignedBB toAABB()
	{
		return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}
}
