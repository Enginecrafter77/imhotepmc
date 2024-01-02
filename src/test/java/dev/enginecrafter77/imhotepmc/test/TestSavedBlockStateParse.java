package dev.enginecrafter77.imhotepmc.test;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.blueprint.SavedBlockState;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestSavedBlockStateParse {
	@Test
	public void testCase1()
	{
		SavedBlockState control = new SavedBlockState(new ResourceLocation("minecraft:stone"), ImmutableMap.of());
		SavedBlockState cmp = SavedBlockState.parse("minecraft:stone");

		Assertions.assertEquals(control, cmp);
	}

	@Test
	public void testCase2()
	{
		SavedBlockState control = new SavedBlockState(new ResourceLocation("minecraft:stone"), ImmutableMap.of("variant", "stone"));
		SavedBlockState cmp = SavedBlockState.parse("minecraft:stone[variant=stone]");

		Assertions.assertEquals(control, cmp);
	}
}
