package dev.enginecrafter77.imhotepmc.test.math;

import dev.enginecrafter77.imhotepmc.util.math.Box3d;
import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import dev.enginecrafter77.imhotepmc.util.math.Edge3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.vecmath.Point3d;
import java.util.Objects;
import java.util.Random;

public class Box3dTest {
	private final Random rng;

	public Box3dTest()
	{
		this.rng = new Random();
	}

	private void randomBox(Box3d box)
	{
		box.set(rng.nextDouble(), rng.nextDouble(), rng.nextDouble(), rng.nextDouble(), rng.nextDouble(), rng.nextDouble());
	}

	@Test
	public void equals_EqualBoxes_ShouldBeEqual()
	{
		Box3d first = new Box3d();
		Box3d second = new Box3d();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 1, 1, 1);
		Assertions.assertEquals(first, second);
	}

	@Test
	public void equals_DifferInX1_ShouldNotBeEqual()
	{
		Box3d first = new Box3d();
		Box3d second = new Box3d();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(1, 0, 0, 1, 1, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInY1_ShouldNotBeEqual()
	{
		Box3d first = new Box3d();
		Box3d second = new Box3d();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 1, 0, 1, 1, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInZ1_ShouldNotBeEqual()
	{
		Box3d first = new Box3d();
		Box3d second = new Box3d();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 1, 1, 1, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInX2_ShouldNotBeEqual()
	{
		Box3d first = new Box3d();
		Box3d second = new Box3d();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 2, 1, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInY2_ShouldNotBeEqual()
	{
		Box3d first = new Box3d();
		Box3d second = new Box3d();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 1, 2, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInZ2_ShouldNotBeEqual()
	{
		Box3d first = new Box3d();
		Box3d second = new Box3d();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 1, 1, 2);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void hashcode_EqualBoxes_ReturnsSameHash()
	{
		Box3d first = new Box3d();
		Box3d second = new Box3d();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 1, 1, 1);
		Assertions.assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	public void set_FromOtherBox_ShouldBeEqual()
	{
		Box3d first = new Box3d();
		this.randomBox(first);
		Box3d second = new Box3d();

		second.set(first);

		Assertions.assertEquals(first, second);
	}

	@Test
	public void set_FromTwoKnownPointValues_EqualsKnown()
	{
		Box3d exp = new Box3d();
		exp.set(1, 1, 1, 0, 0, 0); // reverse the points

		assertBoxEquals(exp, 0, 0, 0, 1, 1, 1);
	}

	@Test
	public void set_FromTwoKnownPoints_EqualsKnown()
	{
		Point3d p1 = new Point3d(0, 0, 0);
		Point3d p2 = new Point3d(1, 1, 1);
		Box3d exp = new Box3d();
		exp.set(p2, p1);

		assertBoxEquals(exp, 0, 0, 0, 1, 1, 1);
	}

	@Test
	public void set_FromKnownBox3i_EqualsKnown()
	{
		Box3i box = new Box3i();
		box.set(0, 0, 0, 1, 1, 1);

		Box3d box3d = new Box3d();
		box3d.set(box);

		assertBoxEquals(box3d, 0, 0, 0, 1, 1, 1);
	}

	@Test
	public void translate_KnownBoxByKnownAmount_EqualsKnown()
	{
		Box3d actual = new Box3d();
		actual.set(1, 1, 1, 2, 2, 2);

		actual.translate(2, 2, 2);

		assertBoxEquals(actual, 3, 3, 3, 4, 4, 4);
	}

	@Test
	public void moveTo_KnownBoxToKnownPoint_EqualsKnown()
	{
		Box3d actual = new Box3d();
		actual.set(1, 1, 1, 2, 2, 2);

		actual.moveTo(3, 3, 3);

		assertBoxEquals(actual, 3, 3, 3, 4, 4, 4);
	}

	@Test
	public void setSize_KnownBoxToKnownSize_EqualsKnown()
	{
		Box3d actual = new Box3d();
		actual.set(1, 1, 1, 1, 1, 1);

		actual.setSize(2, 2, 2);

		assertBoxEquals(actual, 1, 1, 1, 3, 3, 3);
	}

	@Test
	public void setSize_NegativeSizeValue_CoordinatesRearrange()
	{
		Box3d actual = new Box3d();
		actual.set(3, 3, 3, 4, 4, 4);

		actual.setSize(-2, -2, -2);

		assertBoxEquals(actual, 1, 1, 1, 3, 3, 3);
	}

	@Test
	public void setSize_NegativeSizeValue_SizesMatchAbsolute()
	{
		Box3d actual = new Box3d();
		actual.set(3, 3, 3, 4, 4, 4);

		actual.setSize(-2, -2, -2);

		Assertions.assertEquals(2, actual.getSizeX());
		Assertions.assertEquals(2, actual.getSizeY());
		Assertions.assertEquals(2, actual.getSizeZ());
	}

	@Test
	public void setSize_IrregularNegativeSizeValue_CoordinatesRearrange()
	{
		Box3d actual = new Box3d();
		actual.set(4, 4, 4, 4, 4, 4);

		actual.setSize(-1, 2, -3);

		assertBoxEquals(actual, 3, 4, 1, 4, 6, 4);
	}

	@Test
	public void setSize_IrregularNegativeSizeValue_SizesMatchAbsolute()
	{
		Box3d actual = new Box3d();
		actual.set(4, 4, 4, 4, 4, 4);

		actual.setSize(-1, 2, -3);

		Assertions.assertEquals(1, actual.getSizeX());
		Assertions.assertEquals(2, actual.getSizeY());
		Assertions.assertEquals(3, actual.getSizeZ());
	}

	@Test
	public void grow_KnownBoxByKnownValues_EqualsKnown()
	{
		Box3d actual = new Box3d();
		actual.set(1, 1, 1, 2, 2, 2);

		actual.grow(1, 1, 1);

		assertBoxEquals(actual, 1, 1, 1, 3, 3, 3);
	}

	@Test
	public void getSize_KnownBox_EqualsKnownValues()
	{
		Box3d actual = new Box3d();
		actual.set(1, 1, 1, 2, 3, 4);

		Assertions.assertEquals(1, actual.getSizeX());
		Assertions.assertEquals(2, actual.getSizeY());
		Assertions.assertEquals(3, actual.getSizeZ());

		Point3d exp = new Point3d(1, 2, 3);
		Point3d out = new Point3d();
		actual.getSize(out);
		Assertions.assertEquals(exp, out);
	}

	@Test
	public void intersect_TwoKnownBoxes_EqualsKnownBox()
	{
		Box3d b1 = new Box3d();
		b1.set(2, 2, 2, 5, 5, 5);
		Box3d b2 = new Box3d();
		b2.set(0, 0, 0, 3, 3, 3);

		Box3d bi = new Box3d();
		bi.set(b1);
		bi.intersect(b2);

		assertBoxEquals(bi, 2, 2, 2, 3, 3, 3);
	}

	@Test
	public void union_TwoKnownBoxes_EqualsKnownBox()
	{
		Box3d b1 = new Box3d();
		b1.set(2, 2, 2, 5, 5, 5);
		Box3d b2 = new Box3d();
		b2.set(0, 0, 0, 3, 3, 3);

		Box3d bi = new Box3d();
		bi.set(b1);
		bi.union(b2);

		assertBoxEquals(bi, 0, 0, 0, 5, 5, 5);
	}

	@Test
	public void include_TwoKnownBoxAndNewPoint_EqualsKnownBox()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 2, 2, 2);

		b1.include(3, 3, 3);

		assertBoxEquals(b1, 1, 1, 1, 3, 3, 3);
	}

	@Test
	public void include_TwoKnownBoxAndIncludedPoint_EqualsKnownBox()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 2, 2, 2);

		b1.include(1, 1, 1);

		assertBoxEquals(b1, 1, 1, 1, 2, 2, 2);
	}

	@Test
	public void include_TwoKnownBoxAndCornerPoint_EqualsKnownBox()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 2, 2, 2);

		b1.include(2, 2, 2);

		assertBoxEquals(b1, 1, 1, 1, 2, 2, 2);
	}

	@Test
	public void volume_KnownBox_EqualsKnownValue()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertEquals(64, b1.volume());
	}

	@Test
	public void contains_KnownBoxInternalPoint_EqualsTrue()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertTrue(b1.contains(2, 2, 2));
	}

	@Test
	public void contains_KnownBoxOutsidePoint_EqualsTrue()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertFalse(b1.contains(10, 10, 10));
	}

	@Test
	public void contains_KnownBoxLowestPoint_EqualsTrue()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertTrue(b1.contains(1, 1, 1));
	}

	@Test
	public void contains_KnownBoxHighestPoint_EqualsTrue()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertFalse(b1.contains(5, 5, 5));
	}

	@Test
	public void corners_KnownBox_EqualsKnownValues()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 4, 4, 4);

		assertElementCount(b1.corners(), 8);
		assertPointExists(b1.corners(), 1, 1, 1);
		assertPointExists(b1.corners(), 1, 1, 4);
		assertPointExists(b1.corners(), 1, 4, 1);
		assertPointExists(b1.corners(), 1, 4, 4);
		assertPointExists(b1.corners(), 4, 1, 1);
		assertPointExists(b1.corners(), 4, 1, 4);
		assertPointExists(b1.corners(), 4, 4, 1);
		assertPointExists(b1.corners(), 4, 4, 4);
	}

	@Test
	public void edges_KnownBox_EqualsKnownValues()
	{
		Box3d b1 = new Box3d();
		b1.set(1, 1, 1, 4, 4, 4);

		assertElementCount(b1.edges(), 12);
		assertEdgeExists(b1.edges(), 1, 1, 1, 4, 1, 1);
		assertEdgeExists(b1.edges(), 4, 1, 1, 4, 1, 4);
		assertEdgeExists(b1.edges(), 4, 1, 4, 1, 1, 4);
		assertEdgeExists(b1.edges(), 1, 1, 4, 1, 1, 1);
		assertEdgeExists(b1.edges(), 1, 4, 1, 4, 4, 1);
		assertEdgeExists(b1.edges(), 4, 4, 1, 4, 4, 4);
		assertEdgeExists(b1.edges(), 4, 4, 4, 1, 4, 4);
		assertEdgeExists(b1.edges(), 1, 4, 4, 1, 4, 1);
		assertEdgeExists(b1.edges(), 1, 1, 1, 1, 4, 1);
		assertEdgeExists(b1.edges(), 4, 1, 1, 4, 4, 1);
		assertEdgeExists(b1.edges(), 4, 1, 4, 4, 4, 4);
		assertEdgeExists(b1.edges(), 1, 1, 4, 1, 4, 4);
	}

	private static void assertElementCount(Iterable<?> itr, int count)
	{
		int counted = 0;
		for(Object v : itr)
			++counted;
		Assertions.assertEquals(count, counted);
	}

	private static void assertEdgeExists(Iterable<Edge3d> edges, double x1, double y1, double z1, double x2, double y2, double z2)
	{
		Edge3d searched = new Edge3d();
		searched.set(x1, y1, z1, x2, y2, z2);
		assertContains(edges, searched);
	}

	private static void assertPointExists(Iterable<Point3d> points, int x, int y, int z)
	{
		assertContains(points, new Point3d(x, y, z));
	}

	private static <T> void assertContains(Iterable<T> itr, T value)
	{
		for(T v : itr)
		{
			if(Objects.equals(v, value))
				return;
		}
		Assertions.fail("Object " + itr + " does not contain value " + value);
	}

	private static void assertBoxEquals(Box3d box, double x1, double y1, double z1, double x2, double y2, double z2)
	{
		Box3d expected = new Box3d();
		expected.start.set(x1, y1, z1);
		expected.end.set(x2, y2, z2);
		Assertions.assertEquals(expected, box);
	}
}
