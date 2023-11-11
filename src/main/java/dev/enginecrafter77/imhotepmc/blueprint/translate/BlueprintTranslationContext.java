package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.SavedTileState;
import net.minecraft.util.math.BlockPos;

public interface BlueprintTranslationContext {
	public SavedTileState getTileState(BlockPos pos);
}
