package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.blueprint.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class BlueprintSerializerTest<T> {
	public abstract BlueprintSerializer<T> createSerializer();

	@Test
	public void testSerializeDeserialize()
	{
		SchematicBlueprint blueprint = this.createBlueprint();
		BlueprintSerializer<T> serializer = this.createSerializer();
		T serialized = serializer.serializeBlueprint(blueprint);
		SchematicBlueprint rec = serializer.deserializeBlueprint(serialized);
		Assertions.assertEquals(blueprint, rec);
	}

	private StructureBlueprint createRegion()
	{
		if(!Bootstrap.isRegistered())
			Bootstrap.register();

		SavedTileState block = SavedTileState.ofBlock(Blocks.IRON_BLOCK);

		BlueprintEditor blueprintEditor = StructureBlueprint.begin();
		blueprintEditor.addBlock(new BlockPos(14, 1, 14), block);
		blueprintEditor.addBlock(new BlockPos(13, 1, 14), block);
		blueprintEditor.addBlock(new BlockPos(14, 1, 13), block);
		blueprintEditor.addBlock(new BlockPos(15, 1, 14), block);
		blueprintEditor.addBlock(new BlockPos(14, 1, 15), block);
		blueprintEditor.addBlock(new BlockPos(14, 0, 14), block);
		blueprintEditor.addBlock(new BlockPos(14, 2, 14), block);
		return blueprintEditor.build();
	}

	private SchematicBlueprint createBlueprint()
	{
		StructureBlueprint region = this.createRegion();
		SchematicEditor builder = SchematicBlueprint.builder();
		builder.addRegion("Main", region, BlockPos.ORIGIN);
		return builder.build();
	}
}
