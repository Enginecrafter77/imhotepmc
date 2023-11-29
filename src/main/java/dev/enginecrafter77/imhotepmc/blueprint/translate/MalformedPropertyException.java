package dev.enginecrafter77.imhotepmc.blueprint.translate;

public class MalformedPropertyException extends Exception {
	private final String propertyBundle;

	public MalformedPropertyException(String propertyBundle)
	{
		super("Malformed property expression: " + propertyBundle);
		this.propertyBundle = propertyBundle;
	}

	public String getPropertyBundle()
	{
		return this.propertyBundle;
	}
}
