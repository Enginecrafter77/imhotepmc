package dev.enginecrafter77.imhotepmc.test;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import dev.enginecrafter77.imhotepmc.blueprint.SavedBlockState;
import dev.enginecrafter77.imhotepmc.blueprint.SavedTileState;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslationContext;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslationTable;
import dev.enginecrafter77.imhotepmc.blueprint.translate.CompiledTranslationRule;
import dev.enginecrafter77.imhotepmc.blueprint.translate.MalformedTranslationRuleException;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestTranslationRules {
	@BeforeAll
	public static void initBootstrap()
	{
		if(Bootstrap.isRegistered())
			Bootstrap.register();
	}

	@Test
	public void testCompileMalformedTranslation1()
	{
		Assertions.assertThrows(MalformedTranslationRuleException.class, () -> CompiledTranslationRule.compile("minecraft:spruce_planks"));
	}

	@Test
	public void testCompileMalformedTranslation2()
	{
		Assertions.assertThrows(MalformedTranslationRuleException.class, () -> CompiledTranslationRule.compile("minecraft:spruce_planks => "));
	}

	@Test
	public void testCompileMalformedTranslation3()
	{
		Assertions.assertThrows(MalformedTranslationRuleException.class, () -> CompiledTranslationRule.compile(" => minecraft:spruce_planks"));
	}

	@Test
	public void testCompileMalformedTranslation4()
	{
		Assertions.assertThrows(MalformedTranslationRuleException.class, () -> CompiledTranslationRule.compile("minecraft:oak_planks[vfx] => minecraft:planks"));
	}

	@Test
	public void testCompileMalformedTranslation5()
	{
		Assertions.assertThrows(MalformedTranslationRuleException.class, () -> CompiledTranslationRule.compile("minecraft:oak_planks[vfx=] => minecraft:planks"));
	}

	@Test
	public void testCompileMalformedTranslation6()
	{
		Assertions.assertThrows(MalformedTranslationRuleException.class, () -> CompiledTranslationRule.compile("minecraft$oak_planks => minecraft:planks"));
	}

	@Test
	public void testCompileMalformedTranslation7()
	{
		Assertions.assertThrows(MalformedTranslationRuleException.class, () -> CompiledTranslationRule.compile("minecraft:oak_planks => minecraft$planks"));
	}

	@Test
	public void testBasicTranslation() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_planks"), ImmutableMap.of()), null);
		CompiledTranslationRule rule = CompiledTranslationRule.compile("minecraft:oak_planks => minecraft:planks");

		Assertions.assertTrue(rule.isApplicable(st1));

		BlueprintEntry translated = rule.apply(st1);
		Assertions.assertNotNull(translated);

		Assertions.assertEquals("minecraft:planks", translated.getBlockName().toString());
	}

	@Test
	public void testNonMatchingTranslation() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_planks"), ImmutableMap.of()), null);
		CompiledTranslationRule rule = CompiledTranslationRule.compile("minecraft:spruce_planks => minecraft:planks");
		Assertions.assertFalse(rule.isApplicable(st1));
	}

	@Test
	public void testApplicablePropertyTranslation() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "4")), null);
		CompiledTranslationRule rule = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=4] => minecraft:standing_sign[rotation=8]");

		Assertions.assertTrue(rule.isApplicable(st1));

		BlueprintEntry translated = rule.apply(st1);
		Assertions.assertNotNull(translated);

		Assertions.assertEquals("minecraft:standing_sign", translated.getBlockName().toString());
		Assertions.assertEquals("8", translated.getBlockProperties().get("rotation"));
	}

	@Test
	public void testInapplicablePropertyTranslation() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "5")), null);
		CompiledTranslationRule rule = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=4] => minecraft:standing_sign[rotation=8]");
		Assertions.assertFalse(rule.isApplicable(st1));
	}

	@Test
	public void testTemplatedPropertyTranslation() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "5")), null);
		CompiledTranslationRule rule = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=%1] => minecraft:standing_sign[rotation=%1]");

		Assertions.assertTrue(rule.isApplicable(st1));

		BlueprintEntry translated = rule.apply(st1);
		Assertions.assertNotNull(translated);

		Assertions.assertEquals("minecraft:standing_sign", translated.getBlockName().toString());
		Assertions.assertEquals("5", translated.getBlockProperties().get("rotation"));
	}

	@Test
	public void testWeighedTableTranslation1() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "5")), null);
		CompiledTranslationRule rule1 = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=5] => minecraft:stone");
		CompiledTranslationRule rule2 = CompiledTranslationRule.compile("minecraft:oak_sign => minecraft:dirt");
		BlueprintTranslationTable table = BlueprintTranslationTable.compile(rule1, rule2);

		BlueprintEntry translated = table.translate(BlueprintTranslationContext.dummy(), BlockPos.ORIGIN, st1);
		Assertions.assertNotNull(translated);

		Assertions.assertEquals("minecraft:stone", translated.getBlockName().toString());
	}

	@Test
	public void testWeighedTableTranslation2() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "6")), null);
		CompiledTranslationRule rule1 = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=5] => minecraft:stone");
		CompiledTranslationRule rule2 = CompiledTranslationRule.compile("minecraft:oak_sign => minecraft:dirt");
		BlueprintTranslationTable table = BlueprintTranslationTable.compile(rule1, rule2);

		BlueprintEntry translated = table.translate(BlueprintTranslationContext.dummy(), BlockPos.ORIGIN, st1);
		Assertions.assertNotNull(translated);

		Assertions.assertEquals("minecraft:dirt", translated.getBlockName().toString());
	}

	@Test
	public void testWeighedTemplateTableTranslation1() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "4")), null);
		CompiledTranslationRule rule1 = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=4] => minecraft:stone");
		CompiledTranslationRule rule2 = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=%1] => minecraft:dirt");
		BlueprintTranslationTable table = BlueprintTranslationTable.compile(rule1, rule2);

		BlueprintEntry translated = table.translate(BlueprintTranslationContext.dummy(), BlockPos.ORIGIN, st1);
		Assertions.assertNotNull(translated);

		Assertions.assertEquals("minecraft:stone", translated.getBlockName().toString());
	}

	@Test
	public void testWeighedTemplateTableTranslation2() throws MalformedTranslationRuleException
	{
		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "5")), null);
		CompiledTranslationRule rule1 = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=4] => minecraft:stone");
		CompiledTranslationRule rule2 = CompiledTranslationRule.compile("minecraft:oak_sign[rotation=%1] => minecraft:dirt");
		BlueprintTranslationTable table = BlueprintTranslationTable.compile(rule1, rule2);

		BlueprintEntry translated = table.translate(BlueprintTranslationContext.dummy(), BlockPos.ORIGIN, st1);
		Assertions.assertNotNull(translated);

		Assertions.assertEquals("minecraft:dirt", translated.getBlockName().toString());
	}

	@Test
	public void testNoCopyTileEntity() throws MalformedTranslationRuleException
	{
		NBTTagCompound tile = new NBTTagCompound();
		tile.setString("text", "abcd");

		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "5")), tile);
		CompiledTranslationRule rule = CompiledTranslationRule.compile("minecraft:oak_sign => minecraft:standing_sign");

		Assertions.assertTrue(rule.isApplicable(st1));

		BlueprintEntry translated = rule.apply(st1);
		Assertions.assertNotNull(translated);
		Assertions.assertFalse(translated.hasTileEntity());
	}

	@Test
	public void testCopyTileEntity() throws MalformedTranslationRuleException
	{
		NBTTagCompound tile = new NBTTagCompound();
		tile.setString("text", "abcd");

		SavedTileState st1 = new SavedTileState(new SavedBlockState(new ResourceLocation("minecraft:oak_sign"), ImmutableMap.of("rotation", "5")), tile);
		CompiledTranslationRule rule = CompiledTranslationRule.compile("minecraft:oak_sign{*} => minecraft:standing_sign{*}");

		Assertions.assertTrue(rule.isApplicable(st1));

		BlueprintEntry translated = rule.apply(st1);
		Assertions.assertNotNull(translated);
		Assertions.assertTrue(translated.hasTileEntity());
		Assertions.assertEquals(tile, translated.getTileEntitySavedData());
	}
}
