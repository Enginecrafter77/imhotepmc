package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;

import java.util.Comparator;

public interface RuleCondition {
	public boolean evaluate(BlueprintEntry entry);

	public int weight();

	public static Comparator<RuleCondition> weightAscending()
	{
		return Comparator.comparing(RuleCondition::weight);
	}
}
