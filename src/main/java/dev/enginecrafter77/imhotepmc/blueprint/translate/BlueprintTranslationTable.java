package dev.enginecrafter77.imhotepmc.blueprint.translate;

import com.google.common.collect.ImmutableList;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import dev.enginecrafter77.imhotepmc.util.ImmutableListCollector;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlueprintTranslationTable implements BlueprintTranslation {
	private final List<BlueprintTranslationRule> rules;

	public BlueprintTranslationTable(List<BlueprintTranslationRule> rules)
	{
		this.rules = rules;
	}

	@Nullable
	@Override
	public BlueprintEntry translate(BlueprintTranslationContext context, BlockPos position, BlueprintEntry old)
	{
		for(BlueprintTranslationRule rule : this.rules)
		{
			if(rule.isApplicable(old))
				return rule.apply(old);
		}
		return old;
	}

	public static BlueprintTranslationTable compile(Collection<BlueprintTranslationRule> rules)
	{
		ImmutableList<BlueprintTranslationRule> rulePriorityList = rules.stream()
				.sorted(BlueprintTranslationRule.priorityComparator())
				.collect(ImmutableListCollector.get());
		return new BlueprintTranslationTable(rulePriorityList);
	}

	public static BlueprintTranslationTable compile(BlueprintTranslationRule... rules)
	{
		return compile(Arrays.asList(rules));
	}
}
