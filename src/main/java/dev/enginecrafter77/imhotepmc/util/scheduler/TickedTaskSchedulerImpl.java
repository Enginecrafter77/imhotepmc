package dev.enginecrafter77.imhotepmc.util.scheduler;

import net.minecraft.util.ITickable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TickedTaskSchedulerImpl implements TickedTaskScheduler, ITickable {
	private static final Logger LOGGER = LogManager.getLogger(TickedTaskSchedulerImpl.class);

	private final List<EnqueuedTickedTask<?>> tasks;

	public TickedTaskSchedulerImpl()
	{
		this.tasks = new ArrayList<EnqueuedTickedTask<?>>();
	}

	public void dropAllTasks()
	{
		this.tasks.clear();
	}

	@Override
	public <T> EnqueuedTickedTask<T> enqueue(TickedTask<T> task)
	{
		EnqueuedTickedTask<T> enqTask = new EnqueuedTickedTask<T>(task);
		this.tasks.add(enqTask);
		return enqTask;
	}

	@Override
	public void update()
	{
		Iterator<EnqueuedTickedTask<?>> itr = this.tasks.iterator();
		while(itr.hasNext())
		{
			EnqueuedTickedTask<?> task = itr.next();
			try
			{
				task.update();
			}
			catch(Throwable error)
			{
				LOGGER.error("Error ticking task", error);
				itr.remove();
				continue;
			}

			if(task.isDone())
				itr.remove();
		}
	}
}
