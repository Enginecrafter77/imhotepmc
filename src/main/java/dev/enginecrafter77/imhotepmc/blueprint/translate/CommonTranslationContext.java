package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.SavedTileState;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public class CommonTranslationContext implements BlueprintTranslationContext {
	private final Function<BlockPos, SavedTileState> fetcher;
	private final BlueprintTranslation translation;

	public CommonTranslationContext(BlueprintTranslation translation, Function<BlockPos, SavedTileState> fetcher)
	{
		this.translation = translation;
		this.fetcher = fetcher;
	}

	@Override
	public SavedTileState getTileState(BlockPos pos)
	{
		SavedTileState fetched = this.fetcher.apply(pos);
		fetched = this.translation.translate(this, pos, fetched);
		return fetched;
	}
}
