package dev.enginecrafter77.imhotepmc.blueprint.translate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class BlueprintGameVersionTranslator implements BlueprintCrossVersionTable {
	private final Map<Integer, BlueprintVersionTranslationEntry> tables;
	@Nullable
	private final BlueprintTranslationTable fallback;
	private final int producedVersion;

	public BlueprintGameVersionTranslator(Map<Integer, BlueprintVersionTranslationEntry> tables, int producedVersion, @Nullable BlueprintTranslationTable fallback)
	{
		this.fallback = fallback;
		this.producedVersion = producedVersion;
		this.tables = tables;
	}

	@Override
	public Set<Integer> getAcceptedDataVersions()
	{
		return this.tables.keySet();
	}

	@Override
	public int getProducedDataVersion()
	{
		return this.producedVersion;
	}

	@Nullable
	@Override
	public BlueprintCrossVersionTranslation getTranslationFor(int version)
	{
		BlueprintCrossVersionTranslation translation = this.tables.get(version);
		if(translation == null && this.fallback != null)
			translation = new BlueprintVersionTranslationEntry(this.fallback, version, this.producedVersion);
		return translation;
	}

	public static class BlueprintVersionTranslationEntry implements BlueprintCrossVersionTranslation
	{
		private final BlueprintTranslationTable table;
		private final int acceptedVersion;
		private final int producedVersion;

		public BlueprintVersionTranslationEntry(BlueprintTranslationTable table, int acceptedVersion, int producedVersion)
		{
			this.table = table;
			this.acceptedVersion = acceptedVersion;
			this.producedVersion = producedVersion;
		}

		public BlueprintTranslationTable getTable()
		{
			return this.table;
		}

		@Override
		public int getAcceptedDataVersion()
		{
			return this.acceptedVersion;
		}

		@Override
		public int getProducedDataVersion()
		{
			return this.producedVersion;
		}

		@Nullable
		@Override
		public BlueprintEntry translate(BlueprintTranslationContext context, BlockPos position, BlueprintEntry old)
		{
			return this.table.translate(context, position, old);
		}
	}

	public static class VersionTranslatorBuilder
	{
		private final Map<Integer, Collection<BlueprintTranslationRule>> ruleListMap;
		private final Multimap<Integer, BlueprintTranslationRule> ruleMultimap;
		private final int versionTo;

		private int defaultVersionFrom;

		public VersionTranslatorBuilder(int versionTo)
		{
			this.ruleListMap = Maps.newHashMap();
			this.ruleMultimap = Multimaps.newListMultimap(this.ruleListMap, Lists::newArrayList);
			this.defaultVersionFrom = -1;
			this.versionTo = versionTo;
		}

		public int getVersionTo()
		{
			return this.versionTo;
		}

		public int getDefaultVersionFrom()
		{
			return this.defaultVersionFrom;
		}

		public void setDefaultVersionFrom(int defaultVersionFrom)
		{
			this.defaultVersionFrom = defaultVersionFrom;
		}

		public void addRule(int versionFrom, BlueprintTranslationRule rule)
		{
			this.ruleMultimap.put(versionFrom, rule);
		}

		public void removeRule(int versionFrom, BlueprintTranslationRule rule)
		{
			this.ruleMultimap.remove(versionFrom, rule);
		}

		@Nullable
		public BlueprintTranslationRule findRule(int versionFrom, Predicate<BlueprintTranslationRule> filter)
		{
			return this.ruleMultimap.get(versionFrom).stream().filter(filter).findAny().orElse(null);
		}

		public void clearTable(int versionTo)
		{
			this.ruleMultimap.removeAll(versionTo);
		}

		public void appendTable(int versionFrom, BlueprintTranslationTable table)
		{
			this.ruleMultimap.putAll(versionFrom, table.getRules());
		}

		public void setTable(int versionFrom, BlueprintTranslationTable table)
		{
			this.clearTable(versionFrom);
			this.appendTable(versionFrom, table);
		}

		protected BlueprintVersionTranslationEntry createEntry(Integer versionFrom, Collection<BlueprintTranslationRule> rules)
		{
			BlueprintTranslationTable table = BlueprintTranslationTable.compile(rules);
			return new BlueprintVersionTranslationEntry(table, versionFrom, this.versionTo);
		}

		public BlueprintGameVersionTranslator build()
		{
			Map<Integer, BlueprintVersionTranslationEntry> entry = Maps.transformEntries(this.ruleListMap, this::createEntry);
			@Nullable BlueprintTranslationTable fallback = null;
			if(this.defaultVersionFrom != -1)
				fallback = entry.get(this.defaultVersionFrom).getTable();
			return new BlueprintGameVersionTranslator(entry, this.versionTo, fallback);
		}
	}
}
