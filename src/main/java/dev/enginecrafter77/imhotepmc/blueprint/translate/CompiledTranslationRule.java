package dev.enginecrafter77.imhotepmc.blueprint.translate;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import dev.enginecrafter77.imhotepmc.blueprint.SavedBlockState;
import dev.enginecrafter77.imhotepmc.blueprint.SavedTileState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CompiledTranslationRule implements BlueprintTranslationRule {
	private final List<RuleCondition> conditions;

	private final Map<String, EntryValueExtractor> variableExtractors;

	private final String replacementName;
	private final Map<String, String> properties;
	private final boolean copyTE;

	public CompiledTranslationRule(List<RuleCondition> conditions, Map<String, EntryValueExtractor> variableExtractors, String replacementName, Map<String, String> properties, boolean copyTE)
	{
		this.variableExtractors = variableExtractors;
		this.conditions = conditions;
		this.properties = properties;
		this.replacementName = replacementName;
		this.copyTE = copyTE;
	}

	@Override
	public boolean isApplicable(BlueprintEntry entry)
	{
		for(RuleCondition condition : this.conditions)
		{
			if(!condition.evaluate(entry))
				return false;
		}
		return true;
	}

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("%([0-9]+)");
	protected String substituteVariables(String src, Map<String, String> substitutions)
	{
		StringBuilder builder = new StringBuilder(src);
		Matcher matcher = VARIABLE_PATTERN.matcher(builder);
		while(matcher.find())
		{
			String key = matcher.group(1);
			String val = substitutions.get(key);
			if(val == null)
				val = "";
			builder.replace(matcher.start(), matcher.end(), val);
			matcher.reset();
		}
		return builder.toString();
	}

	@Nullable
	@Override
	public BlueprintEntry apply(BlueprintEntry entry)
	{
		Map<String, String> substitutions = new HashMap<String, String>();
		for(String key : this.variableExtractors.keySet())
		{
			EntryValueExtractor extractor = this.variableExtractors.get(key);
			String value = extractor.getValue(entry);
			substitutions.put(key, value);
		}

		Map<String, String> props = new HashMap<String, String>(this.properties);
		for(String key : props.keySet())
		{
			String val = props.get(key);
			val = this.substituteVariables(val, substitutions);
			props.put(key, val);
		}

		String blockName = this.substituteVariables(this.replacementName, substitutions);
		SavedBlockState sbs = new SavedBlockState(new ResourceLocation(blockName), props);

		@Nullable NBTTagCompound tile = this.copyTE ? entry.getTileEntitySavedData() : null;
		return new SavedTileState(sbs, tile);
	}

	@Override
	public int getPriority()
	{
		return Integer.MAX_VALUE - this.conditions.stream().mapToInt(RuleCondition::weight).sum();
	}

	private static final Pattern ENTRY_VAL = Pattern.compile("([A-Za-z0-9_%-]+:[A-Za-z0-9_%-]+)(?:\\[((?:[a-zA-Z0-9_]+=[a-zA-Z0-9_%]+\\s*,?\\s*)*)])?(?:\\{(\\*)})?");
	public static CompiledTranslationRule compile(String src) throws MalformedTranslationRuleException
	{
		String[] sides = src.split("\\s*=>\\s*");
		if(sides.length != 2)
			throw new MalformedTranslationRuleException(src, "Rule requires exactly two operands between => operator");
		String lval = sides[0];
		String rval = sides[1];

		Matcher lmatcher = ENTRY_VAL.matcher(lval);
		if(!lmatcher.matches())
			throw new MalformedTranslationRuleException(src, "L-value does not match rule pattern");
		Matcher rmatcher = ENTRY_VAL.matcher(rval);
		if(!rmatcher.matches())
			throw new MalformedTranslationRuleException(src, "R-value does not match rule pattern");

		String lname = lmatcher.group(1);
		String rname = rmatcher.group(1);

		ImmutableMap.Builder<String, EntryValueExtractor> extractors = ImmutableMap.builder();
		Set<RuleCondition> conditions = new HashSet<RuleCondition>();
		Map<String, String> rprop = ImmutableMap.of();

		conditions.add(new NameMatchingCondition(new ResourceLocation(lname)));

		String lpropBundle = lmatcher.group(2);
		String rpropBundle = rmatcher.group(2);

		try
		{
			if(lpropBundle != null)
			{
				Map<String, String> lprop = parseProperties(lpropBundle);
				for(String key : lprop.keySet())
				{
					String val = lprop.get(key);
					Matcher matcher = VARIABLE_PATTERN.matcher(val);
					if(matcher.matches())
					{
						extractors.put(matcher.group(1), EntryValueExtractor.property(key));
					}
					else
					{
						conditions.add(new PropertyMatchingCondition(key, val));
					}
				}
			}

			if(rpropBundle != null)
				rprop = parseProperties(rpropBundle);
		}
		catch(MalformedPropertyException exc)
		{
			throw new MalformedTranslationRuleException(src, "Property expression parsing failed", exc);
		}

		boolean copyTE = Objects.equals(lmatcher.group(3), "*") && Objects.equals(rmatcher.group(3), "*");

		List<RuleCondition> weighedConditions = conditions.stream().sorted(RuleCondition.weightAscending()).collect(Collectors.toList());
		return new CompiledTranslationRule(weighedConditions, extractors.build(), rname, rprop, copyTE);
	}

	public static Map<String, String> parseProperties(String propBundle) throws MalformedPropertyException
	{
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		String[] parts = propBundle.split("\\s*,\\s*");
		for(String part : parts)
		{
			String[] lr = part.split("=");
			if(lr.length != 2)
				throw new MalformedPropertyException(propBundle);
			builder.put(lr[0], lr[1]);
		}
		return builder.build();
	}

	public static interface EntryValueExtractor
	{
		public String getValue(BlueprintEntry entry);

		public static EntryValueExtractor property(String name)
		{
			return (BlueprintEntry entry) -> entry.getBlockProperties().get(name);
		}
	}

}
