package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;

import java.util.Objects;

public class PropertyMatchingCondition implements RuleCondition {
	private final String key;
	private final String value;

	public PropertyMatchingCondition(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean evaluate(BlueprintEntry entry)
	{
		return Objects.equals(entry.getBlockProperties().get(this.key), this.value);
	}

	@Override
	public int weight()
	{
		return 10;
	}
}
