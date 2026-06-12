package dev.enginecrafter77.imhotepmc.util;

import dev.enginecrafter77.imhotepmc.util.scheduler.EnqueuedTickedTask;
import dev.enginecrafter77.imhotepmc.util.scheduler.TickedTask;
import dev.enginecrafter77.imhotepmc.util.scheduler.TickedTaskSchedulerImpl;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerBackgroundTaskScheduler {
	private final TickedTaskSchedulerImpl scheduler;

	public ServerBackgroundTaskScheduler()
	{
		this.scheduler = new TickedTaskSchedulerImpl();
	}

	public void dropAllTasks()
	{
		this.scheduler.dropAllTasks();
	}

	public <T> EnqueuedTickedTask<T> enqueue(TickedTask<T> task)
	{
		return this.scheduler.enqueue(task);
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		this.scheduler.update();
	}
}
