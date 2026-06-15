package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.util.BlockPosIntegerPacker;
import dev.enginecrafter77.imhotepmc.util.FastBlockPosList;
import dev.enginecrafter77.imhotepmc.util.FastBlockPosSet;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class FastBlockPosUtilsTest {
	@Test
	public void testPackerSerdeRound()
	{
		BlockPos pos = new BlockPos(42, 42, 42);
		int data = BlockPosIntegerPacker.packRelativeBlockPos(pos);
		BlockPos res = BlockPosIntegerPacker.unpackRelativeBlockPos(data);
		Assertions.assertEquals(pos, res);
	}

	@Test
	public void testPackerSerdeOutOfRange()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			BlockPosIntegerPacker.packRelativeBlockPos(new BlockPos(4096, 0, 0));
		});
	}

	@Test
	public void testListAddGet()
	{
		FastBlockPosList list = new FastBlockPosList();
		list.add(new BlockPos(-1, -1, -1));

		Assertions.assertEquals(1, list.size());
		Assertions.assertEquals(new BlockPos(-1, -1, -1), list.get(0));
	}

	@Test
	public void testRandomListEquivalency()
	{
		FastBlockPosList list = new FastBlockPosList();
		List<BlockPos> arrayList = new ArrayList<BlockPos>();

		Random rng = new Random();
		for(int i = 0; i < 4096; ++i)
		{
			BlockPos pos = new BlockPos(rng.nextInt() % 1024, rng.nextInt() % 512, rng.nextInt() % 1024);
			list.add(pos);
			arrayList.add(pos);
		}

		for(int i = 0; i < 4096; ++i)
		{
			Assertions.assertEquals(arrayList.get(i), list.get(i));
		}
	}

	@Test
	public void testSetAddContains()
	{
		FastBlockPosSet set = new FastBlockPosSet();
		set.add(new BlockPos(0, 0, 0));
		set.add(new BlockPos(1, 1, 1));

		Assertions.assertTrue(set.contains(new BlockPos(0, 0, 0)));
		Assertions.assertTrue(set.contains(new BlockPos(1, 1, 1)));
		Assertions.assertFalse(set.contains(new BlockPos(-1, -1, -1)));
	}

	@Test
	public void testRandomSetEquivalency()
	{
		FastBlockPosSet set = new FastBlockPosSet();
		Set<BlockPos> refSet = new TreeSet<BlockPos>();

		Random rng = new Random();
		for(int i = 0; i < 4096; ++i)
		{
			BlockPos pos = new BlockPos(rng.nextInt() % 1024, rng.nextInt() % 512, rng.nextInt() % 1024);
			set.add(pos);
			refSet.add(pos);
		}

		assertSetsEqual(set, refSet);
	}

	private static <T> void assertSetsEqual(Set<T> s1, Set<T> s2)
	{
		for(T pos : s1)
		{
			Assertions.assertTrue(s2.contains(pos));
		}
		for(T pos : s2)
		{
			Assertions.assertTrue(s1.contains(pos));
		}
	}
}
