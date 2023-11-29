package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface BlueprintTranslation {
	@Nullable
	public BlueprintEntry translate(BlueprintTranslationContext context, BlockPos position, BlueprintEntry old);

	public static BlueprintTranslation pass()
	{
		return (BlueprintTranslationContext context, BlockPos position, BlueprintEntry old) -> old;
	}
}
