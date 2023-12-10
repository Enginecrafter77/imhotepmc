package dev.enginecrafter77.imhotepmc.blueprint.builder;

import javax.annotation.Nullable;

public interface BuilderMaterialProvider {
	@Nullable
	public BuilderMaterialStorage getBuilderMaterialStorage();
}
