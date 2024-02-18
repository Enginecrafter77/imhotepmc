package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.SaveableStateHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class BuilderWrapper implements ITickable, SaveableStateHolder<NBTTagCompound> {
	@Nullable
	private StructureBuilder builder;

	@Nullable
	private World world;

	@Nonnull
	private TickedBuilderTaskExecutor taskExecutor;

	protected boolean done;

	public BuilderWrapper()
	{
		this.taskExecutor = new TickedBasicBuilderTaskExecutor();
		this.builder = null;
		this.done = false;
		this.world = null;
	}

	@Nullable
	public StructureBuilder getBuilder()
	{
		return this.builder;
	}

	public void setBuilder(@Nullable StructureBuilder builder)
	{
		this.taskExecutor.cancelAll();
		this.builder = builder;
		this.done = false;
	}

	@Nullable
	public BuilderTask getLastTask()
	{
		if(this.builder == null || this.world == null)
			return null;
		return this.builder.getLastTask(this.world);
	}

	public void setTaskExecutor(TickedBuilderTaskExecutor executor)
	{
		this.taskExecutor = executor;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	public boolean isDone()
	{
		return this.done;
	}

	@Override
	public void update()
	{
		if(this.done || this.builder == null || this.world == null)
			return;

		if(this.taskExecutor.isReady())
		{
			if(!this.builder.nextTask(this.world))
			{
				this.done = true;
				return;
			}
			BuilderTask task = Objects.requireNonNull(this.builder.getLastTask(this.world));
			this.taskExecutor.submit(task);
		}
		this.taskExecutor.update();
	}

	@Override
	public NBTTagCompound saveState()
	{
		NBTTagCompound tag = Optional.ofNullable(this.builder).map(SaveableStateHolder::saveState).orElseGet(NBTTagCompound::new);
		tag.setBoolean("present", this.builder != null);
		tag.setBoolean("done", this.done);
		return tag;
	}

	@Override
	public void restoreState(NBTTagCompound nbt)
	{
		this.done = nbt.getBoolean("done");
		boolean present = nbt.getBoolean("present");
		if(this.builder == null || !present)
			return;
		this.builder.restoreState(nbt);
	}
}
