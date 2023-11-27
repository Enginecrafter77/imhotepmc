package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.blueprint.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.Instant;

public class TestBlueprintLitematicaSerialize {
	@Test
	public void testSimpleSerialize()
	{
		if(!Bootstrap.isRegistered())
			Bootstrap.register();

		SavedTileState block = SavedTileState.ofBlock(Blocks.IRON_BLOCK);

		BlueprintEditor blueprintEditor = RegionBlueprint.begin();
		blueprintEditor.addBlock(new BlockPos(14, 1, 14), block);
		blueprintEditor.addBlock(new BlockPos(13, 1, 14), block);
		blueprintEditor.addBlock(new BlockPos(14, 1, 13), block);
		blueprintEditor.addBlock(new BlockPos(15, 1, 14), block);
		blueprintEditor.addBlock(new BlockPos(14, 1, 15), block);
		blueprintEditor.addBlock(new BlockPos(14, 0, 14), block);
		blueprintEditor.addBlock(new BlockPos(14, 2, 14), block);
		RegionBlueprint region = blueprintEditor.build();

		SchematicBlueprint.Builder schematicBuilder = SchematicBlueprint.builder();
		MutableSchematicMetadata metadata = new MutableSchematicMetadata();
		metadata.setName("TEST1");
		metadata.setAuthor("TESTER");
		metadata.setDescription("Testing schematic");
		metadata.setCreateTime(Instant.now().minusSeconds(10));
		metadata.setModifyTime(Instant.now());
		schematicBuilder.setMetadata(metadata);
		schematicBuilder.addRegion("Main", region, new BlockPos(2, 2, 2));
		SchematicBlueprint blueprint = schematicBuilder.build();

		LitematicaBlueprintSerializer serializer = new LitematicaBlueprintSerializer();

		Assertions.assertDoesNotThrow(() -> {
			NBTTagCompound com = serializer.serializeBlueprint(blueprint);
			SchematicBlueprint rec = serializer.deserializeBlueprint(com);

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
			SchematicBlueprint blueprint = serializer.deserializeBlueprint(tag);

			NBTTagCompound res = serializer.serializeBlueprint(blueprint);
			SchematicBlueprint rec = serializer.deserializeBlueprint(res);

			Assertions.assertEquals(blueprint, rec);
		}
		catch(Exception exc)
		{
			Assertions.fail(exc);
		}
	}
}
