package dev.enginecrafter77.imhotepmc.util.math;

import dev.enginecrafter77.imhotepmc.util.ArrayIterator;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3i;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class Box3i {
	private final Corners corners;
	private final Edges edges;

	public final Point3i start; // Inclusive
	public final Point3i end; // Exclusive

	public Box3i()
	{
		this.corners = new Corners();
		this.edges = new Edges();
		this.start = new Point3i();
		this.end = new Point3i();
	}

	public void set(Box3i other)
	{
		this.start.set(other.start);
		this.end.set(other.end);
	}

	public void set(Point3i p1, Point3i p2)
	{
		this.set(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}

	public void set(Vec3i p1, Vec3i p2)
	{
		this.set(p1.getX(), p1.getY(), p1.getZ(), p2.getX(), p2.getY(), p2.getZ());
	}

	public void set(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		this.start.x = Math.min(x1, x2);
		this.start.y = Math.min(y1, y2);
		this.start.z = Math.min(z1, z2);
		this.end.x = Math.max(x1, x2);
		this.end.y = Math.max(y1, y2);
		this.end.z = Math.max(z1, z2);
	}

	public void translate(int x, int y, int z)
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

	public void moveTo(int x, int y, int z)
	{
		this.translate(x - this.start.x, y - this.start.y, z - this.start.z);
	}

	public void moveTo(Point3i point)
	{
		this.moveTo(point.x, point.y, point.z);
	}

	public void setSize(int xs, int ys, int zs)
	{
		this.end.x = this.start.x + xs;
		this.end.y = this.start.y + ys;
		this.end.z = this.start.z + zs;
		this.consolidate();
	}

	public void grow(int x, int y, int z)
	{
		this.end.x += x;
		this.end.y += y;
		this.end.z += z;
	}

	public void grow(Tuple3i size)
	{
		this.grow(size.x, size.y, size.z);
	}

	public int getSizeX()
	{
		return this.end.x - this.start.x;
	}

	public int getSizeY()
	{
		return this.end.y - this.start.y;
	}

	public int getSizeZ()
	{
		return this.end.z - this.start.z;
	}

	public void getSize(Tuple3i out)
	{
		out.x = this.getSizeX();
		out.y = this.getSizeY();
		out.z = this.getSizeZ();
	}

	private void consolidate()
	{
		if(this.end.x < this.start.x)
		{
			int tmpX = this.start.x;
			this.start.x = this.end.x;
			this.end.x = tmpX;
		}

		if(this.end.y < this.start.y)
		{
			int tmpY = this.start.y;
			this.start.y = this.end.y;
			this.end.y = tmpY;
		}

		if(this.end.z < this.start.z)
		{
			int tmpZ = this.start.z;
			this.start.z = this.end.z;
			this.end.z = tmpZ;
		}
	}

	public void intersect(Box3i other)
	{
		int maxStartX = Math.max(this.start.x, other.start.x);
		int maxStartY = Math.max(this.start.y, other.start.y);
		int maxStartZ = Math.max(this.start.z, other.start.z);
		int minEndX = Math.min(this.end.x, other.end.x);
		int minEndY = Math.min(this.end.y, other.end.y);
		int minEndZ = Math.min(this.end.z, other.end.z);
		this.start.set(maxStartX, maxStartY, maxStartZ);
		this.end.set(minEndX, minEndY, minEndZ);
	}

	public void union(Box3i other)
	{
		int minStartX = Math.min(this.start.x, other.start.x);
		int minStartY = Math.min(this.start.y, other.start.y);
		int minStartZ = Math.min(this.start.z, other.start.z);
		int maxEndX = Math.max(this.end.x, other.end.x);
		int maxEndY = Math.max(this.end.y, other.end.y);
		int maxEndZ = Math.max(this.end.z, other.end.z);
		this.start.set(minStartX, minStartY, minStartZ);
		this.end.set(maxEndX, maxEndY, maxEndZ);
	}

	public void include(int x, int y, int z)
	{
		this.start.x = Math.min(this.start.x, x);
		this.start.y = Math.min(this.start.y, y);
		this.start.z = Math.min(this.start.z, z);
		this.end.x = Math.max(this.end.x, x+1);
		this.end.y = Math.max(this.end.y, y+1);
		this.end.z = Math.max(this.end.z, z+1);
	}

	public void include(Point3i point)
	{
		this.include(point.x, point.y, point.z);
	}

	public void include(Vec3i point)
	{
		this.include(point.getX(), point.getY(), point.getZ());
	}

	public int volume()
	{
		return this.getSizeX() * this.getSizeY() * this.getSizeZ();
	}

	public boolean contains(int x, int y, int z)
	{
		return x >= this.start.x && x < this.end.x &&
				y >= this.start.y && y < this.end.y &&
				z >= this.start.z && z < this.end.z;
	}

	public boolean contains(Point3i point)
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
		if(!(obj instanceof Box3i))
			return false;
		Box3i other = (Box3i)obj;
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
		return String.format("Box(%d:%d:%d/%d:%d:%d)", this.start.x, this.start.y, this.start.z, this.end.x, this.end.y, this.end.z);
	}

	public class Corners implements Iterable<Point3i>
	{
		private final Point3i[] corners;
		private int validForHash;

		public Corners()
		{
			this.corners = new Point3i[8];
			this.validForHash = 0;
			Arrays.setAll(this.corners, i -> new Point3i());
		}
		
		public void get(int corner, Point3i out)
		{
			out.set(this.corners[corner]);
		}

		void update()
		{
			Box3i b = Box3i.this;
			if(this.validForHash == b.hashCode())
				return;
			this.corners[0].set(b.start.x, b.start.y, b.start.z);
			this.corners[1].set(b.end.x-1, b.start.y, b.start.z);
			this.corners[2].set(b.end.x-1, b.start.y, b.end.z-1);
			this.corners[3].set(b.start.x, b.start.y, b.end.z-1);

			this.corners[4].set(b.start.x, b.end.y-1, b.start.z);
			this.corners[5].set(b.end.x-1, b.end.y-1, b.start.z);
			this.corners[6].set(b.end.x-1, b.end.y-1, b.end.z-1);
			this.corners[7].set(b.start.x, b.end.y-1, b.end.z-1);
			this.validForHash = b.hashCode();
		}

		@Nonnull
		@Override
		public Iterator<Point3i> iterator()
		{
			return new ArrayIterator<>(this.corners, 0, this.corners.length, 0);
		}
	}

	public class Edges implements Iterable<Edge3i>
	{
		private final Edge3i[] edges;
		private int validForHash;

		public Edges()
		{
			this.edges = new Edge3i[12];
			this.validForHash = 0;
			Arrays.setAll(this.edges, i -> new Edge3i());
		}

		void update()
		{
			Box3i b = Box3i.this;
			if(this.validForHash == b.hashCode())
				return;
			Corners c = b.corners();
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
		public Iterator<Edge3i> iterator()
		{
			return new ArrayIterator<>(this.edges, 0, this.edges.length, 0);
		}
	}
}
