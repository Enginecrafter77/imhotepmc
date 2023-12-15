package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;

import javax.annotation.Nullable;
import java.util.UUID;

public interface BlueprintPlacementProvider {
	@Nullable
	public BlueprintPlacement getPlacement();
	public boolean isInvalid();
	public boolean isPlacementVisible();
	public long getPlacementProviderUniqueId();
}
