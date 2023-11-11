package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.SavedTileState;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface BlueprintTranslation {
	@Nullable
	public SavedTileState translate(BlueprintTranslationContext context, BlockPos position, SavedTileState state);

	public static BlueprintTranslation identity()
	{
		return new BlueprintTranslation() {
			@Nullable
			@Override
			public SavedTileState translate(BlueprintTranslationContext context, BlockPos position, SavedTileState state)
			{
				return state;
			}
		};
	}
}
