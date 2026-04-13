package dev.enginecrafter77.imhotepmc.test.math;

import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import dev.enginecrafter77.imhotepmc.util.math.Edge3i;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.vecmath.Point3i;
import java.util.Objects;
import java.util.Random;

public class Box3iTest {
	private final Random rng;

	public Box3iTest()
	{
		this.rng = new Random();
	}

	private void randomBox(Box3i box)
	{
		box.set(rng.nextInt(), rng.nextInt(), rng.nextInt(), rng.nextInt(), rng.nextInt(), rng.nextInt());
	}

	@Test
	public void equals_EqualBoxes_ShouldBeEqual()
	{
		Box3i first = new Box3i();
		Box3i second = new Box3i();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 1, 1, 1);
		Assertions.assertEquals(first, second);
	}

	@Test
	public void equals_DifferInX1_ShouldNotBeEqual()
	{
		Box3i first = new Box3i();
		Box3i second = new Box3i();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(1, 0, 0, 1, 1, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInY1_ShouldNotBeEqual()
	{
		Box3i first = new Box3i();
		Box3i second = new Box3i();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 1, 0, 1, 1, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInZ1_ShouldNotBeEqual()
	{
		Box3i first = new Box3i();
		Box3i second = new Box3i();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 1, 1, 1, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInX2_ShouldNotBeEqual()
	{
		Box3i first = new Box3i();
		Box3i second = new Box3i();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 2, 1, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInY2_ShouldNotBeEqual()
	{
		Box3i first = new Box3i();
		Box3i second = new Box3i();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 1, 2, 1);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void equals_DifferInZ2_ShouldNotBeEqual()
	{
		Box3i first = new Box3i();
		Box3i second = new Box3i();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 1, 1, 2);
		Assertions.assertNotEquals(first, second);
	}

	@Test
	public void hashcode_EqualBoxes_ReturnsSameHash()
	{
		Box3i first = new Box3i();
		Box3i second = new Box3i();
		first.set(0, 0, 0, 1, 1, 1);
		second.set(0, 0, 0, 1, 1, 1);
		Assertions.assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	public void set_FromOtherBox_ShouldBeEqual()
	{
		Box3i first = new Box3i();
		this.randomBox(first);
		Box3i second = new Box3i();

		second.set(first);

		Assertions.assertEquals(first, second);
	}

	@Test
	public void set_FromTwoKnownPointValues_EqualsKnown()
	{
		Box3i exp = new Box3i();
		exp.set(1, 1, 1, 0, 0, 0); // reverse the points

		assertBoxEquals(exp, 0, 0, 0, 1, 1, 1);
	}

	@Test
	public void set_FromTwoKnownPoints_EqualsKnown()
	{
		Point3i p1 = new Point3i(0, 0, 0);
		Point3i p2 = new Point3i(1, 1, 1);
		Box3i exp = new Box3i();
		exp.set(p2, p1);

		assertBoxEquals(exp, 0, 0, 0, 1, 1, 1);
	}

	@Test
	public void translate_KnownBoxByKnownAmount_EqualsKnown()
	{
		Box3i actual = new Box3i();
		actual.set(1, 1, 1, 2, 2, 2);

		actual.translate(2, 2, 2);

		assertBoxEquals(actual, 3, 3, 3, 4, 4, 4);
	}

	@Test
	public void moveTo_KnownBoxToKnownPoint_EqualsKnown()
	{
		Box3i actual = new Box3i();
		actual.set(1, 1, 1, 2, 2, 2);

		actual.moveTo(3, 3, 3);

		assertBoxEquals(actual, 3, 3, 3, 4, 4, 4);
	}

	@Test
	public void setSize_KnownBoxToKnownSize_EqualsKnown()
	{
		Box3i actual = new Box3i();
		actual.set(1, 1, 1, 1, 1, 1);

		actual.setSize(2, 2, 2);

		assertBoxEquals(actual, 1, 1, 1, 3, 3, 3);
	}

	@Test
	public void setSize_NegativeSizeValue_CoordinatesRearrange()
	{
		Box3i actual = new Box3i();
		actual.set(3, 3, 3, 4, 4, 4);

		actual.setSize(-2, -2, -2);

		assertBoxEquals(actual, 1, 1, 1, 3, 3, 3);
	}

	@Test
	public void setSize_NegativeSizeValue_SizesMatchAbsolute()
	{
		Box3i actual = new Box3i();
		actual.set(3, 3, 3, 4, 4, 4);

		actual.setSize(-2, -2, -2);

		Assertions.assertEquals(2, actual.getSizeX());
		Assertions.assertEquals(2, actual.getSizeY());
		Assertions.assertEquals(2, actual.getSizeZ());
	}

	@Test
	public void setSize_IrregularNegativeSizeValue_CoordinatesRearrange()
	{
		Box3i actual = new Box3i();
		actual.set(4, 4, 4, 4, 4, 4);

		actual.setSize(-1, 2, -3);

		assertBoxEquals(actual, 3, 4, 1, 4, 6, 4);
	}

	@Test
	public void setSize_IrregularNegativeSizeValue_SizesMatchAbsolute()
	{
		Box3i actual = new Box3i();
		actual.set(4, 4, 4, 4, 4, 4);

		actual.setSize(-1, 2, -3);

		Assertions.assertEquals(1, actual.getSizeX());
		Assertions.assertEquals(2, actual.getSizeY());
		Assertions.assertEquals(3, actual.getSizeZ());
	}

	@Test
	public void grow_KnownBoxByKnownValues_EqualsKnown()
	{
		Box3i actual = new Box3i();
		actual.set(1, 1, 1, 2, 2, 2);

		actual.grow(1, 1, 1);

		assertBoxEquals(actual, 1, 1, 1, 3, 3, 3);
	}

	@Test
	public void getSize_KnownBox_EqualsKnownValues()
	{
		Box3i actual = new Box3i();
		actual.set(1, 1, 1, 2, 3, 4);

		Assertions.assertEquals(1, actual.getSizeX());
		Assertions.assertEquals(2, actual.getSizeY());
		Assertions.assertEquals(3, actual.getSizeZ());

		Point3i exp = new Point3i(1, 2, 3);
		Point3i out = new Point3i();
		actual.getSize(out);
		Assertions.assertEquals(exp, out);
	}

	@Test
	public void intersect_TwoKnownBoxes_EqualsKnownBox()
	{
		Box3i b1 = new Box3i();
		b1.set(2, 2, 2, 5, 5, 5);
		Box3i b2 = new Box3i();
		b2.set(0, 0, 0, 3, 3, 3);

		Box3i bi = new Box3i();
		bi.set(b1);
		bi.intersect(b2);

		assertBoxEquals(bi, 2, 2, 2, 3, 3, 3);
	}

	@Test
	public void union_TwoKnownBoxes_EqualsKnownBox()
	{
		Box3i b1 = new Box3i();
		b1.set(2, 2, 2, 5, 5, 5);
		Box3i b2 = new Box3i();
		b2.set(0, 0, 0, 3, 3, 3);

		Box3i bi = new Box3i();
		bi.set(b1);
		bi.union(b2);

		assertBoxEquals(bi, 0, 0, 0, 5, 5, 5);
	}

	@Test
	public void include_TwoKnownBoxAndNewPoint_EqualsKnownBox()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 2, 2, 2);

		b1.include(3, 3, 3);

		assertBoxEquals(b1, 1, 1, 1, 4, 4, 4);
	}

	@Test
	public void include_TwoKnownBoxAndIncludedPoint_EqualsKnownBox()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 2, 2, 2);

		b1.include(1, 1, 1);

		assertBoxEquals(b1, 1, 1, 1, 2, 2, 2);
	}

	@Test
	public void include_TwoKnownBoxAndCornerPoint_EqualsKnownBox()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 2, 2, 2);

		b1.include(2, 2, 2);

		assertBoxEquals(b1, 1, 1, 1, 3, 3, 3);
	}

	@Test
	public void volume_KnownBox_EqualsKnownValue()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertEquals(64, b1.volume());
	}

	@Test
	public void contains_KnownBoxInternalPoint_EqualsTrue()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertTrue(b1.contains(2, 2, 2));
	}

	@Test
	public void contains_KnownBoxOutsidePoint_EqualsTrue()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertFalse(b1.contains(10, 10, 10));
	}

	@Test
	public void contains_KnownBoxLowestPoint_EqualsTrue()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertTrue(b1.contains(1, 1, 1));
	}

	@Test
	public void contains_KnownBoxHighestPoint_EqualsTrue()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 5, 5, 5);

		Assertions.assertFalse(b1.contains(5, 5, 5));
	}

	@Test
	public void corners_KnownBox_EqualsKnownValues()
	{
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 5, 5, 5);

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
		Box3i b1 = new Box3i();
		b1.set(1, 1, 1, 5, 5, 5);

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

	private static void assertEdgeExists(Iterable<Edge3i> edges, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		Edge3i searched = new Edge3i();
		searched.set(x1, y1, z1, x2, y2, z2);
		assertContains(edges, searched);
	}

	private static void assertPointExists(Iterable<Point3i> points, int x, int y, int z)
	{
		assertContains(points, new Point3i(x, y, z));
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

	private static void assertBoxEquals(Box3i box, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		Box3i expected = new Box3i();
		expected.start.set(x1, y1, z1);
		expected.end.set(x2, y2, z2);
		Assertions.assertEquals(expected, box);
	}
}
