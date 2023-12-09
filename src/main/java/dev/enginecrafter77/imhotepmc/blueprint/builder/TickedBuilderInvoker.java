package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TickedBuilderInvoker {
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
	public StructureBuilder getBuilder()
	{
		return this.builder;
	}

	public void update(World world)
	{
		if(this.builder == null)
			return;

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
