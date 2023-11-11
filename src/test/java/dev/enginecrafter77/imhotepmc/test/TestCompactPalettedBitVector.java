package dev.enginecrafter77.imhotepmc.test;

import com.google.common.collect.ImmutableList;
import dev.enginecrafter77.imhotepmc.blueprint.CompactPalettedBitVector;
import net.minecraft.nbt.NBTTagLongArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class TestCompactPalettedBitVector {
	@Test
	public void testBasicGetSet()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector = new CompactPalettedBitVector<Integer>(palette, 16);

		vector.set(7, 5);

		Integer rec = vector.get(7);

		Assertions.assertEquals(5, rec);
	}

	@Test
	public void testEquals()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector1 = new CompactPalettedBitVector<Integer>(palette, 16);
		CompactPalettedBitVector<Integer> vector2 = new CompactPalettedBitVector<Integer>(palette, 16);

		vector1.set(7, 5);
		vector2.set(7, 5);

		Assertions.assertEquals(vector1, vector2);
	}

	@Test
	public void testNotEquals()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector1 = new CompactPalettedBitVector<Integer>(palette, 16);
		CompactPalettedBitVector<Integer> vector2 = new CompactPalettedBitVector<Integer>(palette, 16);

		vector1.set(7, 5);
		vector2.set(7, 6);

		Assertions.assertNotEquals(vector1, vector2);
	}

	@Test
	public void testHashEquality()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector1 = new CompactPalettedBitVector<Integer>(palette, 16);
		CompactPalettedBitVector<Integer> vector2 = new CompactPalettedBitVector<Integer>(palette, 16);

		vector1.set(7, 5);
		vector2.set(7, 5);

		Assertions.assertEquals(vector1.hashCode(), vector2.hashCode());
	}

	@Test
	public void testCopyEquals()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector1 = new CompactPalettedBitVector<Integer>(palette, 16);

		vector1.set(7, 5);

		CompactPalettedBitVector<Integer> vector2 = vector1.copy();

		Assertions.assertEquals(vector1, vector2);
	}

	@Test
	public void testSerializeDeserialize()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector = new CompactPalettedBitVector<Integer>(palette, 16);

		NBTTagLongArray tag = vector.serializeNBT();
		CompactPalettedBitVector<Integer> rec = CompactPalettedBitVector.readFromNBT(palette, tag);

		Assertions.assertEquals(vector, rec);
	}

	@Test
	public void testInsertInvalid()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector = new CompactPalettedBitVector<Integer>(palette, 16);

		Assertions.assertThrows(NoSuchElementException.class, () -> {
			vector.set(2, 9);
		});
	}

	@Test
	public void testInsertOOTB()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector = new CompactPalettedBitVector<Integer>(palette, 16);

		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
			vector.set(32, 2);
		});
	}

	@Test
	public void testGetOOTB()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector = new CompactPalettedBitVector<Integer>(palette, 16);

		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
			vector.get(32);
		});
	}

	@Test
	public void testIterate()
	{
		List<Integer> palette = ImmutableList.of(1, 2, 3, 4, 5, 6, 7);
		CompactPalettedBitVector<Integer> vector = new CompactPalettedBitVector<Integer>(palette, 7);

		Random rng = new Random();
		int[] values = new int[vector.getLength()];
		for(int index = 0; index < values.length; ++index)
		{
			values[index] = palette.get(rng.nextInt(palette.size()));

			vector.set(index, values[index]);
		}

		int index = 0;
		for(Integer val : vector)
		{
			int exp = values[index++];
			Assertions.assertEquals(exp, val);
		}
	}
}
