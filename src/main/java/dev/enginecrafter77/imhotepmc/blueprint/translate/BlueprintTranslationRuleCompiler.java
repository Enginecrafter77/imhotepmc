package dev.enginecrafter77.imhotepmc.blueprint.translate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlueprintTranslationRuleCompiler {
	private final List<BlueprintTranslationRule> rules;

	public BlueprintTranslationRuleCompiler()
	{
		this.rules = new ArrayList<BlueprintTranslationRule>();
	}

	public void append(InputStream inputStream) throws MalformedTranslationRuleException
	{
		Scanner scanner = new Scanner(inputStream);
		while(scanner.hasNextLine())
		{
			String line = scanner.nextLine();
			if(line.startsWith("#") || line.isEmpty())
				continue;
			CompiledTranslationRule rule = CompiledTranslationRule.compile(line);
			this.rules.add(rule);
		}
	}

	public BlueprintTranslationTable compile()
	{
		return BlueprintTranslationTable.compile(this.rules);
	}
}
