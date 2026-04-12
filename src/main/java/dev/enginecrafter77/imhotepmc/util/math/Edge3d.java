package dev.enginecrafter77.imhotepmc.util.math;

import dev.enginecrafter77.imhotepmc.util.Axis3d;

import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3d;
import java.util.Objects;

public class Edge3d {
	public final Point3d p1;
	public final Point3d p2;

	public Edge3d()
	{
		this.p1 = new Point3d();
		this.p2 = new Point3d();
	}

	public void set(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		this.p1.x = x1;
		this.p1.y = y1;
		this.p1.z = z1;
		this.p2.x = x2;
		this.p2.y = y2;
		this.p2.z = z2;
	}

	public void set(Point3d p1, Point3d p2)
	{
		this.set(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}

	public void set(Point3i p1, Point3i p2)
	{
		this.set(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}

	public void set(Edge3d other)
	{
		this.set(other.p1, other.p2);
	}

	public void set(Edge3i other)
	{
		this.set(other.p1, other.p2);
	}

	public double deltaX()
	{
		return this.p2.x - this.p1.x;
	}

	public double deltaY()
	{
		return this.p2.y - this.p1.y;
	}

	public double deltaZ()
	{
		return this.p2.z - this.p1.z;
	}

	public void deltas(Tuple3d out)
	{
		out.x = this.deltaX();
		out.y = this.deltaY();
		out.z = this.deltaZ();
	}

	public double lengthSquared()
	{
		return this.deltaX()*this.deltaX() + this.deltaY()*this.deltaY() + this.deltaZ()*this.deltaZ();
	}

	public double length()
	{
		return Math.sqrt(this.lengthSquared());
	}

	public void pointAt(double t, Point3d out)
	{
		out.x = this.p1.x + (this.deltaX() * t);
		out.y = this.p1.y + (this.deltaY() * t);
		out.z = this.p1.z + (this.deltaZ() * t);
	}

	@Nullable
	public Axis3d getConnectingAxis()
	{
		return getConnectingEdgeAxis(this.p1, this.p2);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Edge3d))
			return false;
		Edge3d other = (Edge3d)obj;
		return (Objects.equals(this.p1, other.p1) && Objects.equals(this.p2, other.p2)) ||
				(Objects.equals(this.p1, other.p2) && Objects.equals(this.p2, other.p1));
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.p1, this.p2) + Objects.hash(this.p2, this.p1); // assert that the hash of its opposite equals
	}

	@Nullable
	public static Axis3d getConnectingEdgeAxis(double x1, double y1, double z1, double x2, double y2, double z2)
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
	public static Axis3d getConnectingEdgeAxis(Point3d p1, Point3d p2)
	{
		return getConnectingEdgeAxis(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}
}
