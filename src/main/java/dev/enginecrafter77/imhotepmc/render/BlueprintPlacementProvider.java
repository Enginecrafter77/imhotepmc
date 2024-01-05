package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;

import javax.annotation.Nullable;

public interface BlueprintPlacementProvider {
	@Nullable
	public BlueprintPlacement getPlacement();
	public boolean isPlacementValid();
	public boolean isPlacementVisible();
	public long getPlacementProviderUniqueId();
}
