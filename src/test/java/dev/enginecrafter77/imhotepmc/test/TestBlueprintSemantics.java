package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.blueprint.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBlueprintSemantics {
	@Test
	public void testRegionTotalVolume()
	{
		Blueprint blueprint = this.createRegion();
		Assertions.assertEquals(27, blueprint.getTotalVolume());
	}

	@Test
	public void testRegionSize()
	{
		Blueprint blueprint = this.createRegion();
		Assertions.assertEquals(new Vec3i(3, 3, 3), blueprint.getSize());
	}

	@Test
	public void testRegionBlockCount()
	{
		Blueprint blueprint = this.createRegion();
		Assertions.assertEquals(7, blueprint.getBlockCount());
	}

	@Test
	public void testRegionOrigin()
	{
		Blueprint blueprint = this.createRegion();
		Assertions.assertEquals(BlockPos.ORIGIN, blueprint.getOrigin());
	}

	@Test
	public void testRegionInequityAfterEdit()
	{
		RegionBlueprint region = this.createRegion();
		RegionBlueprint mod = region.edit().addBlock(BlockPos.ORIGIN, SavedBlockState.ofBlock(Blocks.PLANKS)).build();
		Assertions.assertNotEquals(region, mod);
	}

	@Test
	public void testRegionEquityAfterNoopEdit()
	{
		RegionBlueprint region = this.createRegion();
		RegionBlueprint mod = region.edit().build();
		Assertions.assertEquals(region, mod);
	}

	@Test
	public void testRegionHashInequityAfterEdit()
	{
		RegionBlueprint region = this.createRegion();
		RegionBlueprint mod = region.edit().addBlock(BlockPos.ORIGIN, SavedBlockState.ofBlock(Blocks.PLANKS)).build();
		Assertions.assertNotEquals(region.hashCode(), mod.hashCode());
	}

	@Test
	public void testRegionHashEquityAfterNoopEdit()
	{
		RegionBlueprint region = this.createRegion();
		RegionBlueprint mod = region.edit().build();
		Assertions.assertEquals(region.hashCode(), mod.hashCode());
	}

	@Test
	public void testRegionBlockAt()
	{
		Blueprint blueprint = this.createRegion();

		BlueprintEntry block000 = blueprint.getBlockAt(new BlockPos(0, 0, 0));
		BlueprintEntry block111 = blueprint.getBlockAt(new BlockPos(1, 1, 1));

		Assertions.assertNull(block000);
		Assertions.assertNotNull(block111);
		Assertions.assertEquals(Blocks.IRON_BLOCK, block111.getBlock());
	}

	@Test
	public void testSchematicRegionCount()
	{
		SchematicBlueprint blueprint = this.createBlueprint();
		Assertions.assertEquals(1, blueprint.getRegionCount());
	}

	@Test
	public void testSchematicTotalVolume()
	{
		SchematicBlueprint blueprint = this.createBlueprint();
		Assertions.assertEquals(27, blueprint.getTotalVolume());
	}

	@Test
	public void testSchematicBlockCount()
	{
		SchematicBlueprint blueprint = this.createBlueprint();
		Assertions.assertEquals(7, blueprint.getBlockCount());
	}

	@Test
	public void testSchematicSize()
	{
		SchematicBlueprint blueprint = this.createBlueprint();
		Assertions.assertEquals(new Vec3i(3, 3, 3), blueprint.getSize());
	}

	@Test
	public void testSchematicOrigin()
	{
		SchematicBlueprint blueprint = this.createBlueprint();
		Assertions.assertEquals(BlockPos.ORIGIN, blueprint.getOrigin());
	}

	@Test
	public void testSchematicBlockAt()
	{
		Blueprint blueprint = this.createBlueprint();

		BlueprintEntry block000 = blueprint.getBlockAt(new BlockPos(0, 0, 0));
		BlueprintEntry block111 = blueprint.getBlockAt(new BlockPos(1, 1, 1));

		Assertions.assertNull(block000);
		Assertions.assertNotNull(block111);
		Assertions.assertEquals(Blocks.IRON_BLOCK, block111.getBlock());
	}

	@Test
	public void testSchematicAddNonOverlappingRegion()
	{
		SchematicBlueprint schematicBlueprint = this.createBlueprint();
		RegionBlueprint regionBlueprint = this.createRegion();

		Assertions.assertDoesNotThrow(() -> {
			schematicBlueprint.addRegion("New", regionBlueprint, new BlockPos(5, 5, 5));
		});
	}

	@Test
	public void testSchematicAddOverlappingRegion()
	{
		SchematicBlueprint schematicBlueprint = this.createBlueprint();
		RegionBlueprint regionBlueprint = this.createRegion();

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			schematicBlueprint.addRegion("New", regionBlueprint, new BlockPos(1, 1, 1));
		});
	}

	@Test
	public void testMultiregionSchematicBlockAt()
	{
		SchematicBlueprint blueprint = this.createMultiRegionBlueprint();

		BlueprintEntry block111 = blueprint.getBlockAt(new BlockPos(1, 1, 1));
		BlueprintEntry block333 = blueprint.getBlockAt(new BlockPos(3, 3, 3));
		BlueprintEntry block666 = blueprint.getBlockAt(new BlockPos(6, 6, 6));

		Assertions.assertNotNull(block111);
		Assertions.assertNull(block333);
		Assertions.assertNotNull(block666);

		Assertions.assertEquals(block111, block666);
	}

	private RegionBlueprint createRegion()
	{
		if(!Bootstrap.isRegistered())
			Bootstrap.register();

		SavedTileState block = SavedTileState.ofBlock(Blocks.IRON_BLOCK);

		RegionBlueprint.Builder builder = RegionBlueprint.builder();
		builder.addBlock(new BlockPos(14, 1, 14), block);
		builder.addBlock(new BlockPos(13, 1, 14), block);
		builder.addBlock(new BlockPos(14, 1, 13), block);
		builder.addBlock(new BlockPos(15, 1, 14), block);
		builder.addBlock(new BlockPos(14, 1, 15), block);
		builder.addBlock(new BlockPos(14, 0, 14), block);
		builder.addBlock(new BlockPos(14, 2, 14), block);
		return builder.build();
	}

	private SchematicBlueprint createBlueprint()
	{
		RegionBlueprint region = this.createRegion();
		SchematicBlueprint blueprint = new SchematicBlueprint();
		blueprint.addRegion("Main", region, BlockPos.ORIGIN);
		return blueprint;
	}

	private SchematicBlueprint createMultiRegionBlueprint()
	{
		RegionBlueprint region = this.createRegion();
		SchematicBlueprint blueprint = new SchematicBlueprint();
		blueprint.addRegion("Reg1", region, BlockPos.ORIGIN);
		blueprint.addRegion("Reg2", region, new BlockPos(5, 5, 5));
		return blueprint;
	}
}
