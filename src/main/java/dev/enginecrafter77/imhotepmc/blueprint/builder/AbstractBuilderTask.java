package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractBuilderTask implements BuilderTask {
	protected final World world;
	protected final BlockPos pos;

	private boolean done;

	public AbstractBuilderTask(World world, BlockPos pos)
	{
		this.world = world;
		this.pos = pos;
		this.done = false;
	}

	public abstract boolean canBeExecuted();
	public abstract void executeTask();

	@Override
	public BlockPos getPosition()
	{
		return this.pos;
	}

	@Override
	public World getWorld()
	{
		return this.world;
	}

	@Override
	public boolean isDone()
	{
		return this.done;
	}

	@Override
	public void update()
	{
		if(this.done || !this.canBeExecuted())
			return;
		this.executeTask();
		this.done = true;
	}
}
