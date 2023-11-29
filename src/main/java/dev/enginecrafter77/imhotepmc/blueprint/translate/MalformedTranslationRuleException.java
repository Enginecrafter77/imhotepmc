package dev.enginecrafter77.imhotepmc.blueprint.translate;

public class MalformedTranslationRuleException extends Exception {
	private final String rule;

	public MalformedTranslationRuleException(String rule)
	{
		super("Malformed translation rule (" + rule + ")");
		this.rule = rule;
	}

	public MalformedTranslationRuleException(String rule, String reason)
	{
		super("Malformed translation rule (" + rule + "): " + reason);
		this.rule = rule;
	}

	public MalformedTranslationRuleException(String rule, String reason, Throwable cause)
	{
		super("Malformed translation rule (" + rule + "): " + reason, cause);
		this.rule = rule;
	}

	public String getRule()
	{
		return this.rule;
	}
}
