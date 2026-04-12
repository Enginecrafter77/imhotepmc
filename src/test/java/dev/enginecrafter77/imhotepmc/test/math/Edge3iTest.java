package dev.enginecrafter77.imhotepmc.test.math;

import dev.enginecrafter77.imhotepmc.util.Axis3d;
import dev.enginecrafter77.imhotepmc.util.math.Edge3i;
import dev.enginecrafter77.imhotepmc.util.math.NotAxisAlignedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.vecmath.Point3i;

public class Edge3iTest {
	@Test
	public void set_FromValidPoints_EqualsKnown()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 1, 0, 0);

		assertEdgeEquals(edge, 0, 0, 0, 1, 0, 0);
	}

	@Test
	public void set_FromInvalidPoints_ThrowsException()
	{
		Edge3i edge = new Edge3i();
		Assertions.assertThrows(NotAxisAlignedException.class, () -> {
			edge.set(0, 0, 0, 1, 0, 1); // must differ at most in 1 coordinate (look up binary cube for explanation)
		});
	}

	@Test
	public void equals_EqualEdges_ReturnsTrue()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 1, 0, 0);

		Edge3i edge2 = new Edge3i();
		edge2.set(0, 0, 0, 1, 0, 0);

		Assertions.assertEquals(edge, edge2);
	}

	@Test
	public void equals_FlippedEdges_ReturnsTrue()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 1, 0, 0);

		Edge3i edge2 = new Edge3i();
		edge2.set(1, 0, 0, 0, 0, 0);

		Assertions.assertEquals(edge, edge2);
	}

	@Test
	public void equals_DifferentEdges_ReturnsFalse()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 1, 0, 0);

		Edge3i edge2 = new Edge3i();
		edge2.set(0, 0, 0, 0, 1, 0);

		Assertions.assertNotEquals(edge, edge2);
	}

	@Test
	public void hashcode_EqualEdges_ReturnsEqualCode()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 1, 0, 0);

		Edge3i edge2 = new Edge3i();
		edge2.set(0, 0, 0, 1, 0, 0);

		Assertions.assertEquals(edge.hashCode(), edge2.hashCode());
	}

	@Test
	public void hashcode_FlippedEdges_ReturnsEqualCode()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 1, 0, 0);

		Edge3i edge2 = new Edge3i();
		edge2.set(1, 0, 0, 0, 0, 0);

		Assertions.assertEquals(edge.hashCode(), edge2.hashCode());
	}

	@Test
	public void deltas_KnownXEdge_ReturnsKnownValues()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 4, 0, 0);
		Assertions.assertEquals(4, edge.deltaX());
		Assertions.assertEquals(0, edge.deltaY());
		Assertions.assertEquals(0, edge.deltaZ());

		Point3i v = new Point3i();
		edge.deltas(v);
		Assertions.assertEquals(4, v.x);
		Assertions.assertEquals(0, v.y);
		Assertions.assertEquals(0, v.z);
	}

	@Test
	public void deltas_KnownYEdge_ReturnsKnownValues()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 0, 4, 0);
		Assertions.assertEquals(0, edge.deltaX());
		Assertions.assertEquals(4, edge.deltaY());
		Assertions.assertEquals(0, edge.deltaZ());

		Point3i v = new Point3i();
		edge.deltas(v);
		Assertions.assertEquals(0, v.x);
		Assertions.assertEquals(4, v.y);
		Assertions.assertEquals(0, v.z);
	}

	@Test
	public void deltas_KnownZEdge_ReturnsKnownValues()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 0, 0, 4);
		Assertions.assertEquals(0, edge.deltaX());
		Assertions.assertEquals(0, edge.deltaY());
		Assertions.assertEquals(4, edge.deltaZ());

		Point3i v = new Point3i();
		edge.deltas(v);
		Assertions.assertEquals(0, v.x);
		Assertions.assertEquals(0, v.y);
		Assertions.assertEquals(4, v.z);
	}

	@Test
	public void length_KnownXEdge_ReturnsKnownValue()
	{
		Edge3i edge = new Edge3i();

		edge.set(0, 0, 0, 4, 0, 0);

		Assertions.assertEquals(4, edge.length());
	}

	@Test
	public void length_KnownYEdge_ReturnsKnownValue()
	{
		Edge3i edge = new Edge3i();

		edge.set(0, 0, 0, 0, 4, 0);

		Assertions.assertEquals(4, edge.length());
	}

	@Test
	public void length_KnownZEdge_ReturnsKnownValue()
	{
		Edge3i edge = new Edge3i();

		edge.set(0, 0, 0, 0, 0, 4);

		Assertions.assertEquals(4, edge.length());
	}

	@Test
	public void getConnectingAxis_KnownXEdge_ReturnsX()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 4, 0, 0);
		Assertions.assertEquals(Axis3d.X, edge.getConnectingAxis());
	}

	@Test
	public void getConnectingAxis_KnownYEdge_ReturnsY()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 0, 4, 0);
		Assertions.assertEquals(Axis3d.Y, edge.getConnectingAxis());
	}

	@Test
	public void getConnectingAxis_KnownZEdge_ReturnsZ()
	{
		Edge3i edge = new Edge3i();
		edge.set(0, 0, 0, 0, 0, 4);
		Assertions.assertEquals(Axis3d.Z, edge.getConnectingAxis());
	}

	@Test
	public void sGetConnectingEdgeAxis_KnownXEdge_ReturnsX()
	{
		Assertions.assertEquals(Axis3d.X, Edge3i.getConnectingEdgeAxis(0, 0, 0, 1, 0, 0));
	}

	@Test
	public void sGetConnectingEdgeAxis_KnownYEdge_ReturnsY()
	{
		Assertions.assertEquals(Axis3d.Y, Edge3i.getConnectingEdgeAxis(0, 0, 0, 0, 1, 0));
	}

	@Test
	public void sGetConnectingEdgeAxis_KnownZEdge_ReturnsZ()
	{
		Assertions.assertEquals(Axis3d.Z, Edge3i.getConnectingEdgeAxis(0, 0, 0, 0, 0, 1));
	}

	@Test
	public void sGetConnectingEdgeAxis_ZeroLengthEdge_ReturnsNull()
	{
		Assertions.assertNull(Edge3i.getConnectingEdgeAxis(0, 0, 0, 0, 0, 0));
	}

	@Test
	public void sGetConnectingEdgeAxis_NotAxisAlignedIntegerEdge_ReturnsNull()
	{
		Assertions.assertNull(Edge3i.getConnectingEdgeAxis(0, 0, 0, 1, 1, 1));
	}

	private static void assertEdgeEquals(Edge3i edge, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		Edge3i expected = new Edge3i();
		expected.set(x1, y1, z1, x2, y2, z2);
		Assertions.assertEquals(expected, edge);
	}
}
