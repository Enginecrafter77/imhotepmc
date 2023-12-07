package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlockSelectionBoxTest {
	@Test
	public void testSize()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));
		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
	}

	@Test
	public void testSetStartSize()
	{
		BlockPos start = new BlockPos(-1, -1, -1);
		Vec3i size = new Vec3i(3, 3, 3);

		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartSize(start, size);

		Assertions.assertEquals(size, box.getSize());
		Assertions.assertEquals(start, box.getMinCorner());
		Assertions.assertEquals(new BlockPos(1, 1, 1), box.getMaxCorner());
	}

	@Test
	public void testSetStartSizeNegative1()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartSize(new BlockPos(1, 1, 1), new Vec3i(-3, -3, -3));

		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
		Assertions.assertEquals(new BlockPos(-1, -1, -1), box.getMinCorner());
		Assertions.assertEquals(new BlockPos(1, 1, 1), box.getMaxCorner());
	}

	@Test
	public void testSetStartSizeNegative2()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartSize(new BlockPos(1, -1, 1), new Vec3i(-3, 3, -3));

		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
		Assertions.assertEquals(new BlockPos(-1, -1, -1), box.getMinCorner());
		Assertions.assertEquals(new BlockPos(1, 1, 1), box.getMaxCorner());
	}

	@Test
	public void testVolume()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));
		Assertions.assertEquals(27, box.getVolume());
	}

	@Test
	public void testContains()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		Assertions.assertTrue(box.contains(new BlockPos(1, 0, 1)));
		Assertions.assertFalse(box.contains(new BlockPos(0, 2, 0)));
	}

	@Test
	public void testEquity()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		Assertions.assertEquals(box2, box);
	}

	@Test
	public void testUnion()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStartEnd(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));

		box.union(box2);

		Assertions.assertEquals(new Vec3i(4, 4, 4), box.getSize());
		Assertions.assertEquals(64, box.getVolume());
	}

	@Test
	public void testUnionSecondEmpty()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box.union(box2);

		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
		Assertions.assertEquals(27, box.getVolume());
	}

	@Test
	public void testUnionFirstEmpty()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		box.union(box2);

		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
		Assertions.assertEquals(27, box.getVolume());
	}

	@Test
	public void testIntersect()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStartEnd(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2));

		box.intersect(box2);

		Assertions.assertEquals(new Vec3i(2, 2, 2), box.getSize());
		Assertions.assertEquals(8, box.getVolume());
	}

	@Test
	public void testIntersectNonOverlapping()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStartEnd(new BlockPos(5, 5, 5), new BlockPos(8, 8, 8));

		box.intersect(box2);

		Assertions.assertEquals(new Vec3i(0, 0, 0), box.getSize());
		Assertions.assertEquals(0, box.getVolume());
	}

	@Test
	public void testCopy()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.set(box);

		Assertions.assertEquals(box2, box);
	}

	@Test
	public void testSerialize()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		NBTTagCompound tag = box.serializeNBT();
		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.deserializeNBT(tag);

		Assertions.assertEquals(box2, box);
	}

	@Test
	public void testInitRange()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
		Assertions.assertEquals(27, box.getVolume());
	}
}
