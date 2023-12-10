package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.world.World;

public class SynchronousBuilderInvoker extends BuilderInvoker {
	private final StructureBuilder builder;

	public SynchronousBuilderInvoker(StructureBuilder builder)
	{
		this.builder = builder;
	}

	@Override
	public StructureBuilder getBuilder()
	{
		return this.builder;
	}

	public void run(World world)
	{
		while(!this.done)
			this.update(world);
	}
}
