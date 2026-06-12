package dev.enginecrafter77.imhotepmc.util.scheduler;

public interface TickedTaskScheduler {
	public <T> EnqueuedTickedTask<T> enqueue(TickedTask<T> task);
}
