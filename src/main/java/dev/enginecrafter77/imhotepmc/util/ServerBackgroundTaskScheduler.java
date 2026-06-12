package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class ServerBackgroundTaskScheduler {
	private static final Logger LOGGER = LogManager.getLogger(ServerBackgroundTaskScheduler.class);

	private final List<EnqueuedBackgroundTask<?>> tasks;

	public ServerBackgroundTaskScheduler()
	{
		this.tasks = new ArrayList<EnqueuedBackgroundTask<?>>();
	}

	public void dropAllTasks()
	{
		this.tasks.clear();
	}

	public <T> EnqueuedBackgroundTask<T> enqueue(ServerBackgroundTask<T> task)
	{
		EnqueuedBackgroundTask<T> enqTask = new EnqueuedBackgroundTask<T>(task);
		this.tasks.add(enqTask);
		return enqTask;
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		Iterator<EnqueuedBackgroundTask<?>> itr = this.tasks.iterator();
		while(itr.hasNext())
		{
			EnqueuedBackgroundTask<?> task = itr.next();
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

	public static abstract class ServerBackgroundTask<T> implements ITickable {
		@Nullable
		private Result<T> result;

		public ServerBackgroundTask()
		{
			this.result = null;
		}

		protected void onStart() {}

		protected void onCancelled() {}

		protected final void terminate(Result<T> result)
		{
			this.result = result;
		}

		protected final boolean isTerminated()
		{
			return this.result != null;
		}

		public Result<T> runBlocking()
		{
			while(this.result == null)
				this.update();
			return this.result;
		}
	}

	public static abstract class ResultlessBackgroundTask extends ServerBackgroundTask<Object> {
		protected final void complete()
		{
			this.terminate(Result.ok(new Object()));
		}
	}

	public static class EnqueuedBackgroundTask<T> implements ITickable, Future<T>
	{
		private final ServerBackgroundTask<T> task;

		@Nullable
		private Runnable onStartCallback;

		@Nullable
		private Consumer<Result<T>> onCompleteCallback;

		private boolean started;
		private boolean cancelled;

		private final Lock resultLock;
		private final Condition resultPublishedCondition;

		public EnqueuedBackgroundTask(ServerBackgroundTask<T> task)
		{
			this.task = task;
			this.started = false;
			this.cancelled = false;
			this.resultLock = new ReentrantLock();
			this.resultPublishedCondition = this.resultLock.newCondition();
		}

		public EnqueuedBackgroundTask<T> whenStarted(Runnable onStartCallback)
		{
			this.onStartCallback = onStartCallback;
			if(this.started)
				onStartCallback.run();
			return this;
		}

		public EnqueuedBackgroundTask<T> whenComplete(Consumer<Result<T>> onCompleteCallback)
		{
			this.onCompleteCallback = onCompleteCallback;
			if(this.isDone())
				onCompleteCallback.accept(this.task.result);
			return this;
		}

		private void onStarted()
		{
			this.task.onStart();
			if(this.onStartCallback != null)
				this.onStartCallback.run();
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning)
		{
			this.resultLock.lock();
			if(this.isDone())
			{
				this.resultLock.unlock();
				return false;
			}
			this.cancelled = true;
			this.task.result = Result.err(new CancellationException());
			this.resultPublishedCondition.signalAll();
			this.resultLock.unlock();
			return true;
		}

		@Override
		public boolean isCancelled()
		{
			return this.cancelled;
		}

		@Override
		public boolean isDone()
		{
			return this.task.result != null;
		}

		@Override
		public void update()
		{
			if(this.isDone())
				return;

			this.resultLock.lock();
			if(!this.started)
			{
				this.started = true;
				this.onStarted();
			}

			try
			{
				this.task.update();
			}
			catch(Throwable err)
			{
				this.task.terminate(Result.err(err));
			}

			if(this.isDone())
			{
				this.resultPublishedCondition.signalAll();
				if(this.onCompleteCallback != null)
				{
					assert this.task.result != null;
					this.onCompleteCallback.accept(this.task.result);
				}
			}
			this.resultLock.unlock();
		}

		@Override
		public T get() throws InterruptedException, ExecutionException
		{
			this.resultLock.lock();
			while(!this.isDone())
				this.resultPublishedCondition.await();
			Result<T> res = this.task.result;
			this.resultLock.unlock();

			assert res != null;
			if(res.err().isPresent())
				throw new ExecutionException(res.err().get());
			return res.unwrap();
		}

		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
		{
			this.resultLock.lock();
			boolean expired = this.resultPublishedCondition.await(timeout, unit);
			Result<T> res = this.task.result;
			this.resultLock.unlock();

			if(expired)
				throw new TimeoutException();
			assert res != null;
			if(res.err().isPresent())
				throw new ExecutionException(res.err().get());
			return res.unwrap();
		}
	}
}
