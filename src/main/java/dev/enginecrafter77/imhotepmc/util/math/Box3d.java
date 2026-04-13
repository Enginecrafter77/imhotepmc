package dev.enginecrafter77.imhotepmc.util.math;

import dev.enginecrafter77.imhotepmc.util.ArrayIterator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public class Box3d {
	private final Corners corners;
	private final Edges edges;

	public final Point3d start;
	public final Point3d end;

	public Box3d()
	{
		this.corners = new Corners();
		this.edges = new Edges();
		this.start = new Point3d();
		this.end = new Point3d();
	}

	public void set(Box3d other)
	{
		this.start.set(other.start);
		this.end.set(other.end);
	}

	public void set(Box3i other)
	{
		this.set(other.start.x, other.start.y, other.start.z, other.end.x, other.end.y, other.end.z);
	}

	public void set(Point3d p1, Point3d p2)
	{
		this.set(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}

	public void set(Vec3d p1, Vec3d p2)
	{
		this.set(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}

	public void set(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		this.start.x = Math.min(x1, x2);
		this.start.y = Math.min(y1, y2);
		this.start.z = Math.min(z1, z2);
		this.end.x = Math.max(x1, x2);
		this.end.y = Math.max(y1, y2);
		this.end.z = Math.max(z1, z2);
	}

	public void translate(double x, double y, double z)
	{
		this.start.x += x;
		this.start.y += y;
		this.start.z += z;
		this.end.x += x;
		this.end.y += y;
		this.end.z += z;
	}

	public void translate(Tuple3i vec)
	{
		this.translate(vec.x, vec.y, vec.z);
	}

	public void moveTo(double x, double y, double z)
	{
		this.translate(x - this.start.x, y - this.start.y, z - this.start.z);
	}

	public void moveTo(Point3d point)
	{
		this.moveTo(point.x, point.y, point.z);
	}

	public void setSize(double xs, double ys, double zs)
	{
		this.end.x = this.start.x + xs;
		this.end.y = this.start.y + ys;
		this.end.z = this.start.z + zs;
		this.consolidate();
	}

	public void scale(double xs, double ys, double zs)
	{
		this.setSize(xs * this.getSizeX(), ys * this.getSizeY(), zs * this.getSizeZ());
	}

	public void grow(double x, double y, double z)
	{
		this.end.x += x;
		this.end.y += y;
		this.end.z += z;
	}

	public void grow(Tuple3i size)
	{
		this.grow(size.x, size.y, size.z);
	}

	public double getSizeX()
	{
		return this.end.x - this.start.x;
	}

	public double getSizeY()
	{
		return this.end.y - this.start.y;
	}

	public double getSizeZ()
	{
		return this.end.z - this.start.z;
	}

	public void getSize(Tuple3d out)
	{
		out.x = this.getSizeX();
		out.y = this.getSizeY();
		out.z = this.getSizeZ();
	}

	private void consolidate()
	{
		if(this.end.x < this.start.x)
		{
			double tmpX = this.start.x;
			this.start.x = this.end.x;
			this.end.x = tmpX;
		}

		if(this.end.y < this.start.y)
		{
			double tmpY = this.start.y;
			this.start.y = this.end.y;
			this.end.y = tmpY;
		}

		if(this.end.z < this.start.z)
		{
			double tmpZ = this.start.z;
			this.start.z = this.end.z;
			this.end.z = tmpZ;
		}
	}

	public void intersect(Box3d other)
	{
		double maxStartX = Math.max(this.start.x, other.start.x);
		double maxStartY = Math.max(this.start.y, other.start.y);
		double maxStartZ = Math.max(this.start.z, other.start.z);
		double minEndX = Math.min(this.end.x, other.end.x);
		double minEndY = Math.min(this.end.y, other.end.y);
		double minEndZ = Math.min(this.end.z, other.end.z);
		this.start.set(maxStartX, maxStartY, maxStartZ);
		this.end.set(minEndX, minEndY, minEndZ);
	}

	public void union(Box3d other)
	{
		double minStartX = Math.min(this.start.x, other.start.x);
		double minStartY = Math.min(this.start.y, other.start.y);
		double minStartZ = Math.min(this.start.z, other.start.z);
		double maxEndX = Math.max(this.end.x, other.end.x);
		double maxEndY = Math.max(this.end.y, other.end.y);
		double maxEndZ = Math.max(this.end.z, other.end.z);
		this.start.set(minStartX, minStartY, minStartZ);
		this.end.set(maxEndX, maxEndY, maxEndZ);
	}

	public void include(double x, double y, double z)
	{
		this.start.x = Math.min(this.start.x, x);
		this.start.y = Math.min(this.start.y, y);
		this.start.z = Math.min(this.start.z, z);
		this.end.x = Math.max(this.end.x, x);
		this.end.y = Math.max(this.end.y, y);
		this.end.z = Math.max(this.end.z, z);
	}

	public void include(Point3d point)
	{
		this.include(point.x, point.y, point.z);
	}

	public void include(Vec3d point)
	{
		this.include(point.x, point.y, point.z);
	}

	public void include(Vec3i point)
	{
		this.include(point.getX(), point.getY(), point.getZ());
	}

	public double volume()
	{
		return this.getSizeX() * this.getSizeY() * this.getSizeZ();
	}

	public boolean contains(double x, double y, double z)
	{
		return x >= this.start.x && x < this.end.x &&
				y >= this.start.y && y < this.end.y &&
				z >= this.start.z && z < this.end.z;
	}

	public boolean contains(Point3d point)
	{
		return this.contains(point.x, point.y, point.z);
	}

	public boolean contains(Vec3d point)
	{
		return this.contains(point.x, point.y, point.z);
	}

	public boolean contains(Vec3i point)
	{
		return this.contains(point.getX(), point.getY(), point.getZ());
	}

	public Corners corners()
	{
		this.corners.update();
		return this.corners;
	}

	public Edges edges()
	{
		this.edges.update();
		return this.edges;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Box3d))
			return false;
		Box3d other = (Box3d)obj;
		if(this == other)
			return true;
		return Objects.equals(this.start, other.start) && Objects.equals(this.end, other.end);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.start, this.end);
	}

	@Override
	public String toString()
	{
		return String.format("Box(%.02f:%.02f:%.02f/%.02f:%.02f:%.02f)", this.start.x, this.start.y, this.start.z, this.end.x, this.end.y, this.end.z);
	}

	public class Corners implements Iterable<Point3d>
	{
		private final Point3d[] corners;
		private int validForHash;

		public Corners()
		{
			this.corners = new Point3d[8];
			this.validForHash = 0;
			Arrays.setAll(this.corners, i -> new Point3d());
		}

		public void get(int corner, Point3d out)
		{
			out.set(this.corners[corner]);
		}

		void update()
		{
			Box3d b = Box3d.this;
			if(this.validForHash == b.hashCode())
				return;
			this.corners[0].set(b.start.x, b.start.y, b.start.z);
			this.corners[1].set(b.end.x, b.start.y, b.start.z);
			this.corners[2].set(b.end.x, b.start.y, b.end.z);
			this.corners[3].set(b.start.x, b.start.y, b.end.z);

			this.corners[4].set(b.start.x, b.end.y, b.start.z);
			this.corners[5].set(b.end.x, b.end.y, b.start.z);
			this.corners[6].set(b.end.x, b.end.y, b.end.z);
			this.corners[7].set(b.start.x, b.end.y, b.end.z);
			this.validForHash = b.hashCode();
		}

		@Nonnull
		@Override
		public Iterator<Point3d> iterator()
		{
			return new ArrayIterator<>(this.corners, 0, this.corners.length, 0);
		}

		public Stream<Point3d> stream()
		{
			return Arrays.stream(this.corners);
		}
	}

	public class Edges implements Iterable<Edge3d>
	{
		private final Edge3d[] edges;
		private int validForHash;

		public Edges()
		{
			this.edges = new Edge3d[12];
			this.validForHash = 0;
			Arrays.setAll(this.edges, i -> new Edge3d());
		}

		void update()
		{
			Box3d b = Box3d.this;
			if(this.validForHash == b.hashCode())
				return;
			Box3d.Corners c = b.corners();
			this.edges[0].set(c.corners[0], c.corners[1]);
			this.edges[1].set(c.corners[1], c.corners[2]);
			this.edges[2].set(c.corners[2], c.corners[3]);
			this.edges[3].set(c.corners[3], c.corners[0]);

			this.edges[4].set(c.corners[4], c.corners[5]);
			this.edges[5].set(c.corners[5], c.corners[6]);
			this.edges[6].set(c.corners[6], c.corners[7]);
			this.edges[7].set(c.corners[7], c.corners[4]);

			this.edges[8].set(c.corners[0], c.corners[4]);
			this.edges[9].set(c.corners[1], c.corners[5]);
			this.edges[10].set(c.corners[2], c.corners[6]);
			this.edges[11].set(c.corners[3], c.corners[7]);
			this.validForHash = b.hashCode();
		}

		@Nonnull
		@Override
		public Iterator<Edge3d> iterator()
		{
			return new ArrayIterator<>(this.edges, 0, this.edges.length, 0);
		}

		public Stream<Edge3d> stream()
		{
			return Arrays.stream(this.edges);
		}
	}
}
