package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class NameMatchingCondition implements RuleCondition {
	private final ResourceLocation name;

	public NameMatchingCondition(ResourceLocation name)
	{
		this.name = name;
	}

	@Override
	public boolean evaluate(BlueprintEntry entry)
	{
		return Objects.equals(entry.getBlockName(), this.name);
	}

	@Override
	public int weight()
	{
		return 1;
	}
}
