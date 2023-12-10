package dev.enginecrafter77.imhotepmc.blueprint.builder;

import javax.annotation.Nullable;

public class TickedBuilderInvoker extends BuilderInvoker {
	@Nullable
	private StructureBuilder builder;

	public TickedBuilderInvoker()
	{
		this.builder = null;
	}

	public void setBuilder(@Nullable StructureBuilder builder)
	{
		this.builder = builder;
	}

	@Nullable
	@Override
	public StructureBuilder getBuilder()
	{
		return this.builder;
	}
}
