package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;

import javax.annotation.Nullable;
import java.util.Comparator;

public interface BlueprintTranslationRule {
	public boolean isApplicable(BlueprintEntry entry);

	@Nullable
	public BlueprintEntry apply(BlueprintEntry entry);

	public int getPriority();

	public static Comparator<BlueprintTranslationRule> priorityComparator()
	{
		return Comparator.comparing(BlueprintTranslationRule::getPriority);
	}
}
