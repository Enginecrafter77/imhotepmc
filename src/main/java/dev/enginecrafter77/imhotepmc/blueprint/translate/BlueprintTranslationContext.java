package dev.enginecrafter77.imhotepmc.blueprint.translate;

public interface BlueprintTranslationContext {
	public static BlueprintTranslationContext dummy()
	{
		return new BlueprintTranslationContext() {};
	}
}
