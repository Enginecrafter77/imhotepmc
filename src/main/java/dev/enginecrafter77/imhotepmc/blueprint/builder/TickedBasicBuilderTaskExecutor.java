package dev.enginecrafter77.imhotepmc.blueprint.builder;

import javax.annotation.Nullable;

public class TickedBasicBuilderTaskExecutor implements TickedBuilderTaskExecutor {
	@Nullable
	private BuilderTask task;

	public TickedBasicBuilderTaskExecutor()
	{
		this.task = null;
	}

	@Override
	public synchronized void submit(BuilderTask task)
	{
		this.task = task;
	}

	@Override
	public synchronized boolean isReady()
	{
		return this.task == null;
	}

	@Override
	public void cancelAll()
	{
		this.task = null;
	}

	@Override
	public synchronized void update()
	{
		if(this.task != null)
		{
			if(!this.task.canPerformTask())
				return;
			this.task.performTask();
			this.task = null;
		}
	}
}
