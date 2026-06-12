package dev.enginecrafter77.imhotepmc.util.scheduler;

import dev.enginecrafter77.imhotepmc.util.Result;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

public abstract class TickedTask<T> implements ITickable {
	@Nullable
	Result<T> result;

	public TickedTask()
	{
		this.result = null;
	}

	protected void onStart()
	{
	}

	protected void onCancelled()
	{
	}

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

	public static abstract class Resultless extends TickedTask<Object> {
		protected final void complete()
		{
			this.terminate(Result.ok(new Object()));
		}
	}
}
