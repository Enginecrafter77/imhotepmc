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
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));
		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
	}

	@Test
	public void testVolume()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));
		Assertions.assertEquals(27, box.getVolume());
	}

	@Test
	public void testContains()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

		Assertions.assertTrue(box.contains(new Vec3i(1, 0, 1)));
		Assertions.assertFalse(box.contains(new Vec3i(0, 2, 0)));
	}

	@Test
	public void testReset()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));
		box.reset();

		Assertions.assertEquals(0, box.getVolume());
		Assertions.assertEquals(Vec3i.NULL_VECTOR, box.getSize());
	}

	@Test
	public void testEquity()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStart(new BlockPos(-1, -1, -1));
		box2.setEnd(new BlockPos(1, 1, 1));

		Assertions.assertEquals(box2, box);
	}

	@Test
	public void testUnion()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStart(new BlockPos(0, 0, 0));
		box2.setEnd(new BlockPos(2, 2, 2));

		box.union(box2);

		Assertions.assertEquals(new Vec3i(4, 4, 4), box.getSize());
		Assertions.assertEquals(64, box.getVolume());
	}

	@Test
	public void testUnionSecondEmpty()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

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
		box2.setStart(new BlockPos(-1, -1, -1));
		box2.setEnd(new BlockPos(1, 1, 1));

		box.union(box2);

		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
		Assertions.assertEquals(27, box.getVolume());
	}

	@Test
	public void testUnionBothEmpty()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		BlockSelectionBox box2 = new BlockSelectionBox();
		box.union(box2);

		Assertions.assertEquals(Vec3i.NULL_VECTOR, box.getSize());
		Assertions.assertEquals(0, box.getVolume());
	}

	@Test
	public void testIntersect()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStart(new BlockPos(0, 0, 0));
		box2.setEnd(new BlockPos(2, 2, 2));

		box.intersect(box2);

		Assertions.assertEquals(new Vec3i(2, 2, 2), box.getSize());
		Assertions.assertEquals(8, box.getVolume());
	}

	@Test
	public void testIntersectNonOverlapping()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStart(new BlockPos(5, 5, 5));
		box2.setEnd(new BlockPos(8, 8, 8));

		box.intersect(box2);

		Assertions.assertEquals(new Vec3i(0, 0, 0), box.getSize());
		Assertions.assertEquals(0, box.getVolume());
	}

	@Test
	public void testIntersectBothEmpty()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		BlockSelectionBox box2 = new BlockSelectionBox();
		box.intersect(box2);

		Assertions.assertEquals(Vec3i.NULL_VECTOR, box.getSize());
		Assertions.assertEquals(0, box.getVolume());
	}

	@Test
	public void testIntersectSecondEmpty()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box.intersect(box2);

		Assertions.assertEquals(Vec3i.NULL_VECTOR, box.getSize());
		Assertions.assertEquals(0, box.getVolume());
	}

	@Test
	public void testIntersectFirstEmpty()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.setStart(new BlockPos(-1, -1, -1));
		box2.setEnd(new BlockPos(1, 1, 1));

		box.intersect(box2);

		Assertions.assertEquals(Vec3i.NULL_VECTOR, box.getSize());
		Assertions.assertEquals(0, box.getVolume());
	}

	@Test
	public void testCopy()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.set(box);

		Assertions.assertEquals(box2, box);
	}

	@Test
	public void testSerialize()
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStart(new BlockPos(-1, -1, -1));
		box.setEnd(new BlockPos(1, 1, 1));

		NBTTagCompound tag = box.serializeNBT();
		BlockSelectionBox box2 = new BlockSelectionBox();
		box2.deserializeNBT(tag);

		Assertions.assertEquals(box2, box);
	}

	@Test
	public void testInitRange()
	{
		BlockSelectionBox box = new BlockSelectionBox(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));

		Assertions.assertEquals(new Vec3i(3, 3, 3), box.getSize());
		Assertions.assertEquals(27, box.getVolume());
	}
}
