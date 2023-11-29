package dev.enginecrafter77.imhotepmc.blueprint.translate;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlueprintTranslationRuleCompiler {
	private final Scanner scanner;

	public BlueprintTranslationRuleCompiler(InputStream inputStream)
	{
		this.scanner = new Scanner(inputStream);
	}

	public BlueprintTranslationTable compile() throws ParseException
	{
		List<BlueprintTranslationRule> rules = new ArrayList<BlueprintTranslationRule>();
		while(this.scanner.hasNextLine())
		{
			String line = this.scanner.nextLine();
			if(line.startsWith("#") || line.isEmpty())
				continue;
			CompiledTranslationRule rule = CompiledTranslationRule.compile(line);
			rules.add(rule);
		}
		return BlueprintTranslationTable.compile(rules);
	}

}
