package dev.enginecrafter77.imhotepmc.util.math;

import javax.vecmath.Point3i;

public class NotAxisAlignedException extends RuntimeException {
	public final Point3i p1;
	public final Point3i p2;

	public NotAxisAlignedException(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		super(String.format("Edge between points %d:%d:%d and %d:%d:%d is not aligned to any axis.", x1, y1, z1, x2, y2, z2));
		this.p1 = new Point3i(x1, y1, z1);
		this.p2 = new Point3i(x2, y2, z2);
	}

	public NotAxisAlignedException(Point3i p1, Point3i p2)
	{
		this(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}
}
