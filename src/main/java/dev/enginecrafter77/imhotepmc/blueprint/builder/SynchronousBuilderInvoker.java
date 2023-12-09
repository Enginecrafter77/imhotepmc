package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.world.World;

public class SynchronousBuilderInvoker {
	private final StructureBuilder builder;

	public SynchronousBuilderInvoker(StructureBuilder builder)
	{
		this.builder = builder;
	}

	public StructureBuilder getBuilder()
	{
		return this.builder;
	}

	public void run(World world)
	{
		while(true)
		{
			BuilderTask task = this.builder.getLastTask(world);
			if(task == null || task.isDone())
			{
				if(!this.builder.nextTask(world))
					return;
				task = this.builder.getLastTask(world);
			}
			if(task == null)
				return;
			task.update();
		}
	}
}
