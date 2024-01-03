package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SpongeBlueprintSerializer;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestSpongeSerializer extends BlueprintSerializerTest<NBTTagCompound> {
	private static final Log LOGGER = LogFactory.getLog(TestSpongeSerializer.class);

	@Test
	public void testPackedArraySerializeDeserialize()
	{
		int[] vals = new int[] {42, 1200, 36000000, 78};

		byte[] array = SpongeBlueprintSerializer.packSpongeVarintArray(vals);
		int[] rec = SpongeBlueprintSerializer.unpackSpongeVarintArray(array, vals.length);

		Assertions.assertArrayEquals(vals, rec);
	}

	@Test
	public void testPackedArraySerialize()
	{
		int[] vals = new int[] {1200};

		byte[] array = SpongeBlueprintSerializer.packSpongeVarintArray(vals);
		byte[] exp = {(byte)0xB0, (byte)0x09};

		Assertions.assertArrayEquals(exp, array);
	}

	@Test
	public void testPackedArrayDeserialize()
	{
		byte[] array = {(byte)0xB0, (byte)0x09};

		int[] vals = SpongeBlueprintSerializer.unpackSpongeVarintArray(array, 1);
		int[] exp = {1200};

		Assertions.assertArrayEquals(exp, vals);
	}

	public void write(int val)
	{
		LOGGER.info("WRITE: " + Integer.toString(val, 2));
	}

	@Override
	public BlueprintSerializer<NBTTagCompound> createSerializer()
	{
		return new SpongeBlueprintSerializer();
	}
}
