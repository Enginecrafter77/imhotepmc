package dev.enginecrafter77.imhotepmc.blueprint.translate;

public class TranslationNotAvailableException extends Exception {
	private final int from;
	private final int to;

	public TranslationNotAvailableException(int from, int to)
	{
		super(String.format("No available translation from %d -> %d found", from, to));
		this.from = from;
		this.to = to;
	}

	public int getFromVersion()
	{
		return this.from;
	}

	public int getToVersion()
	{
		return this.to;
	}
}
