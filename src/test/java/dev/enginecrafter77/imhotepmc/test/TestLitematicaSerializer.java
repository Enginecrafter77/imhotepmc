package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import net.minecraft.nbt.NBTTagCompound;

public class TestLitematicaSerializer extends BlueprintSerializerTest<NBTTagCompound> {
	@Override
	public BlueprintSerializer<NBTTagCompound> createSerializer()
	{
		return new LitematicaBlueprintSerializer();
	}
}
