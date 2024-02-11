package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerBackgroundTaskScheduler {
	private static final Log LOGGER = LogFactory.getLog(ServerBackgroundTaskScheduler.class);

	private final List<ServerBackgroundTask> tasks;

	public ServerBackgroundTaskScheduler()
	{
		this.tasks = new ArrayList<ServerBackgroundTask>();
	}

	public void dropAllTasks()
	{
		this.tasks.clear();
	}

	public void enqueue(ServerBackgroundTask task)
	{
		this.tasks.add(task);
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		Iterator<ServerBackgroundTask> itr = this.tasks.iterator();
		while(itr.hasNext())
		{
			ServerBackgroundTask task = itr.next();
			try
			{
				task.tick();
			}
			catch(Throwable error)
			{
				LOGGER.error("Error ticking task", error);
				itr.remove();
				continue;
			}

			if(task.isComplete())
				itr.remove();
		}
	}

	public static abstract class ServerBackgroundTask implements ITickable
	{
		@Nullable
		private Runnable onStartCallback;
		@Nullable
		private Runnable onCompleteCallback;

		private boolean started;
		private boolean complete;

		public ServerBackgroundTask()
		{
			this.started = false;
			this.complete = false;
		}

		public void reset()
		{
			this.complete = false;
			this.started = false;
		}

		public void setOnStartCallback(@Nullable Runnable onStartCallback)
		{
			this.onStartCallback = onStartCallback;
		}

		public void setOnCompleteCallback(@Nullable Runnable onCompleteCallback)
		{
			this.onCompleteCallback = onCompleteCallback;
		}

		public boolean isStarted()
		{
			return this.started;
		}

		public boolean isComplete()
		{
			return this.complete;
		}

		protected void markComplete()
		{
			this.complete = true;
		}

		protected void tick()
		{
			if(this.complete)
				return;
			if(!this.started)
			{
				this.started = true;
				if(this.onStartCallback != null)
					this.onStartCallback.run();
			}

			this.update();

			if(this.complete && this.onCompleteCallback != null)
				this.onCompleteCallback.run();
		}

		public void runBlocking()
		{
			while(!this.isComplete())
				this.tick();
		}
	}
}
