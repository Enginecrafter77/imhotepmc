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

	public Edge3d(Tuple3d p1, Tuple3d p2)
	{
		this();
		this.p1.set(p1);
		this.p2.set(p2);
	}

	public Edge3d(Edge3d other)
	{
		this();
		this.p1.set(other.p1);
		this.p2.set(other.p2);
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
		this.p1.set(x1, y1, z1);
		this.p2.set(x2, y2, z2);
	}

	public void set(BlockPosEdge edge, BlockAnchor start, BlockAnchor end)
	{
		start.anchorToBlock(edge.getFirst(), this.p1);
		end.anchorToBlock(edge.getSecond(), this.p2);
	}

	public Tuple3d getFirstPoint()
	{
		return this.p1;
	}

	public Tuple3d getSecondPoint()
	{
		return this.p2;
	}

	public void midpoint(Tuple3d dest)
	{
		VecUtil.midpoint(this.p1, this.p2, dest);
	}

	public void translate(double dx, double dy, double dz)
	{
		this.p1.x += dx;
		this.p1.y += dy;
		this.p1.z += dz;
		this.p2.x += dx;
		this.p2.y += dy;
		this.p2.z += dz;
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

	public static class ImmutableEdge3d extends Edge3d
	{
		public ImmutableEdge3d(Edge3d from)
		{
			super(from);
		}

		public ImmutableEdge3d(Tuple3d p1, Tuple3d p2)
		{
			super(p1, p2);
		}

		@Override
		public void set(Vec3d v1, Vec3d v2)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(Tuple3d p1, Tuple3d p2)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(double x1, double y1, double z1, double x2, double y2, double z2)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void translate(double dx, double dy, double dz)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(BlockPosEdge edge, BlockAnchor start, BlockAnchor end)
		{
			throw new UnsupportedOperationException();
		}
	}
}
