package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class BuilderInvoker {
	protected boolean done;

	public BuilderInvoker()
	{
		this.done = false;
	}

	@Nullable
	public abstract StructureBuilder getBuilder();

	public boolean isDone()
	{
		return this.done;
	}

	public void update(World world)
	{
		if(this.done)
			return;

		StructureBuilder builder = this.getBuilder();
		if(builder == null)
			return;

		BuilderTask task = builder.getLastTask(world);
		if(task == null || task.isDone())
		{
			builder.nextTask(world);
			task = builder.getLastTask(world);
		}

		if(task == null)
		{
			this.done = true;
			return;
		}

		task.update();
	}
}
