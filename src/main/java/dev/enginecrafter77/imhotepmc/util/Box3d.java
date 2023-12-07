package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class Box3d {
	private final Point3d min;
	private final Point3d max;

	public Box3d()
	{
		this.min = new Point3d();
		this.max = new Point3d();
	}

	public Box3d(Tuple3d start, Tuple3d end)
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
		this.min.set(other.min);
		this.max.set(other.max);
	}

	public void set(Tuple3d start, Tuple3d end)
	{
		this.min.x = Math.min(start.x, end.x);
		this.min.y = Math.min(start.y, end.y);
		this.min.z = Math.min(start.z, end.z);
		this.max.x = Math.max(start.x, end.x);
		this.max.y = Math.max(start.y, end.y);
		this.max.z = Math.max(start.z, end.z);
	}

	public void set(AxisAlignedBB box)
	{
		this.min.x = box.minX;
		this.min.y = box.minY;
		this.min.z = box.minZ;
		this.max.x = box.maxX;
		this.max.y = box.maxY;
		this.max.z = box.maxZ;
	}

	public void set(BlockPosBox box)
	{
		BlockPos start = box.getMinCorner();
		BlockPos end = box.getMaxCorner();

		this.min.x = start.getX();
		this.min.y = start.getY();
		this.min.z = start.getZ();
		this.max.x = end.getX() + 1D;
		this.max.y = end.getY() + 1D;
		this.max.z = end.getZ() + 1D;
	}

	public void grow(Tuple3d dim)
	{
		this.grow(dim.x, dim.y, dim.z);
	}

	public void grow(double x, double y, double z)
	{
		this.min.x -= x;
		this.min.y -= y;
		this.min.z -= z;
		this.max.x += x;
		this.max.y += y;
		this.max.z += z;
	}

	public void scale(Tuple3d sc)
	{
		this.scale(sc.x, sc.y, sc.z);
	}

	public void scale(double x, double y, double z)
	{
		double hlx = (this.max.x - this.min.x) / 2D; // half x length
		double hly = (this.max.y - this.min.y) / 2D; // half y length
		double hlz = (this.max.z - this.min.z) / 2D; // half z length

		double mx = this.min.x + hlx;
		double my = this.min.y + hly;
		double mz = this.min.z + hlz;

		hlx *= x;
		hly *= y;
		hlz *= z;

		this.min.x = mx - hlx;
		this.max.y = mx + hlx;
		this.min.z = my - hly;
		this.max.x = my + hly;
		this.min.y = mz - hlz;
		this.max.z = mz + hlz;
	}

	public void translate(double x, double y, double z)
	{
		this.min.x += x;
		this.min.y += y;
		this.min.z += z;
		this.max.x += x;
		this.max.y += y;
		this.max.z += z;
	}

	public void translateTo(double x, double y, double z)
	{
		double dx = x - this.min.x;
		double dy = y - this.min.y;
		double dz = z - this.min.z;
		this.translate(dx, dy, dz);
	}

	public Point3d getStart()
	{
		return this.min;
	}

	public Point3d getEnd()
	{
		return this.max;
	}

	public void getCenter(Tuple3d dest)
	{
		dest.set(this.min);
		dest.add(this.max);
		dest.scale(0.5D);
	}

	public void getSize(Tuple3d dest)
	{
		dest.set(this.max);
		dest.sub(this.min);
	}

	public AxisAlignedBB toAABB()
	{
		return new AxisAlignedBB(this.min.x, this.min.y, this.min.z, this.max.x, this.max.y, this.max.z);
	}
}
