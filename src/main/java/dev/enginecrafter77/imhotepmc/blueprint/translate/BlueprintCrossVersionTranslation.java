package dev.enginecrafter77.imhotepmc.blueprint.translate;

public interface BlueprintCrossVersionTranslation extends BlueprintTranslation {
	public int getAcceptedDataVersion();
	public int getProducedDataVersion();
}
