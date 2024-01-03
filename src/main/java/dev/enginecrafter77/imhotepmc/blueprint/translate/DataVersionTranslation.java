package dev.enginecrafter77.imhotepmc.blueprint.translate;

import java.util.Set;

public interface DataVersionTranslation {
	public int getAcceptedDataVersion();
	public int getProducedDataVersion();
	public Set<BlueprintTranslation> getBlueprintTranslations();
}
