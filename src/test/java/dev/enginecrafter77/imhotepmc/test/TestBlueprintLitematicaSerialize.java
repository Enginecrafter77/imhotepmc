package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.StructureBlockSavedData;
import dev.enginecrafter77.imhotepmc.blueprint.StructureBlueprint;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class TestBlueprintLitematicaSerialize {
	@Test
	public void testSimpleSerialize()
	{
		if(!Bootstrap.isRegistered())
			Bootstrap.register();

		StructureBlockSavedData block = new StructureBlockSavedData(Blocks.IRON_BLOCK.getDefaultState(), null);

		StructureBlueprint.Builder builder = new StructureBlueprint.Builder();
		builder.addBlock(new BlockPos(14, 1, 14), block);
		builder.addBlock(new BlockPos(13, 1, 14), block);
		builder.addBlock(new BlockPos(14, 1, 13), block);
		builder.addBlock(new BlockPos(15, 1, 14), block);
		builder.addBlock(new BlockPos(14, 1, 15), block);
		builder.addBlock(new BlockPos(14, 0, 14), block);
		builder.addBlock(new BlockPos(14, 2, 14), block);
		StructureBlueprint blueprint = builder.build();

		LitematicaBlueprintSerializer serializer = new LitematicaBlueprintSerializer();

		Assertions.assertDoesNotThrow(() -> {
			NBTTagCompound com = serializer.serializeBlueprint(blueprint);
			StructureBlueprint rec = serializer.deserializeBlueprint(com);

			Assertions.assertEquals(blueprint, rec);
		});
	}

	@Test
	public void testLoadRealFile()
	{
		if(!Bootstrap.isRegistered())
			Bootstrap.register();

		InputStream inputStream = TestBlueprintLitematicaSerialize.class.getResourceAsStream("/benchy_OTS.litematic");
		Assertions.assertNotNull(inputStream);

		try
		{
			NBTTagCompound tag = CompressedStreamTools.readCompressed(inputStream);
			LitematicaBlueprintSerializer serializer = new LitematicaBlueprintSerializer();
			StructureBlueprint blueprint = serializer.deserializeBlueprint(tag);
			blueprint.getTotalVolume();
		}
		catch(Exception exc)
		{
			Assertions.fail(exc);
		}
	}
}
