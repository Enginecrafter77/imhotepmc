package dev.enginecrafter77.imhotepmc.util.scheduler;

import dev.enginecrafter77.imhotepmc.util.Result;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class EnqueuedTickedTask<T> implements ITickable, Future<T> {
	private final TickedTask<T> task;

	@Nullable
	private Runnable onStartCallback;

	@Nullable
	private Consumer<Result<T>> onCompleteCallback;

	private boolean started;
	private boolean cancelled;

	private final Lock resultLock;
	private final Condition resultPublishedCondition;

	public EnqueuedTickedTask(TickedTask<T> task)
	{
		this.task = task;
		this.started = false;
		this.cancelled = false;
		this.resultLock = new ReentrantLock();
		this.resultPublishedCondition = this.resultLock.newCondition();
	}

	public EnqueuedTickedTask<T> whenStarted(Runnable onStartCallback)
	{
		this.onStartCallback = onStartCallback;
		if(this.started)
			onStartCallback.run();
		return this;
	}

	public EnqueuedTickedTask<T> whenComplete(Consumer<Result<T>> onCompleteCallback)
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
