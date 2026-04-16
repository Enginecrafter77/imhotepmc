package dev.enginecrafter77.imhotepmc.util.math;

import dev.enginecrafter77.imhotepmc.util.Axis3d;

import javax.annotation.Nullable;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3i;
import java.util.Objects;

public class Edge3i {
	public final Point3i p1;
	public final Point3i p2;

	public Edge3i()
	{
		this.p1 = new Point3i();
		this.p2 = new Point3i();
	}

	public void set(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		if((x1 != x2 || y1 != y2 || z1 != z2) && getConnectingEdgeAxis(x1, y1, z1, x2, y2, z2) == null) // non-zero length edge
			throw new NotAxisAlignedException(x1, y1, z1, x2, y2, z2);
		this.p1.x = x1;
		this.p1.y = y1;
		this.p1.z = z1;
		this.p2.x = x2;
		this.p2.y = y2;
		this.p2.z = z2;
	}

	public void set(Point3i p1, Point3i p2)
	{
		this.set(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}

	public void set(Edge3i other)
	{
		this.set(other.p1, other.p2);
	}

	public int deltaX()
	{
		return this.p2.x - this.p1.x;
	}

	public int deltaY()
	{
		return this.p2.y - this.p1.y;
	}

	public int deltaZ()
	{
		return this.p2.z - this.p1.z;
	}

	public void deltas(Tuple3i out)
	{
		out.x = this.deltaX();
		out.y = this.deltaY();
		out.z = this.deltaZ();
	}

	public int length()
	{
		if(Objects.equals(this.p1, this.p2))
			return 0;
		switch(this.getConnectingAxis())
		{
		case X:
			return Math.abs(this.p1.x - this.p2.x);
		case Y:
			return Math.abs(this.p1.y - this.p2.y);
		case Z:
			return Math.abs(this.p1.z - this.p2.z);
		default:
			throw new IllegalStateException();
		}
	}

	public Axis3d getConnectingAxis()
	{
		return Objects.requireNonNull(getConnectingEdgeAxis(this.p1, this.p2));
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Edge3i))
			return false;
		Edge3i other = (Edge3i)obj;
		return (Objects.equals(this.p1, other.p1) && Objects.equals(this.p2, other.p2)) ||
				(Objects.equals(this.p1, other.p2) && Objects.equals(this.p2, other.p1));
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.p1, this.p2) + Objects.hash(this.p2, this.p1); // assert that the hash of its opposite equals
	}

	@Nullable
	public static Axis3d getConnectingEdgeAxis(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		if(x1 != x2 && y1 == y2 && z1 == z2)
			return Axis3d.X;
		if(x1 == x2 && y1 != y2 && z1 == z2)
			return Axis3d.Y;
		if(x1 == x2 && y1 == y2 && z1 != z2)
			return Axis3d.Z;
		return null;
	}

	@Nullable
	public static Axis3d getConnectingEdgeAxis(Point3i p1, Point3i p2)
	{
		return getConnectingEdgeAxis(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}
}
