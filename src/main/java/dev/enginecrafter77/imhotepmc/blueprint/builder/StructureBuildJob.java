package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.NaturalVoxelIndexer;
import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import dev.enginecrafter77.imhotepmc.util.SaveableStateHolder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class StructureBuildJob implements SaveableStateHolder<NBTTagCompound>, ITickable {
	private static final String NBT_KEY_DEFERRED = "deferred";
	private static final String NBT_KEY_INDEX = "index";

	protected final BlockPos buildOrigin;
	protected final Vec3i buildSize;

	private final IntList deferred;
	private final VoxelIndexer indexer;

	@Nullable
	protected World world;

	@Nullable
	private BuilderTask currentTask;
	private int currentIndex;

	public StructureBuildJob(BlockPos buildOrigin, Vec3i buildSize, VoxelIndexer indexer)
	{
		this.deferred = new IntArrayList();
		this.buildOrigin = buildOrigin;
		this.buildSize = buildSize;
		this.indexer = indexer;
		this.currentTask = null;
		this.currentIndex = 0;
		this.world = null;
	}

	public StructureBuildJob(BlockPos buildOrigin, Vec3i buildSize)
	{
		this(buildOrigin, buildSize, new NaturalVoxelIndexer(buildOrigin, buildSize));
	}

	@Nonnull
	public abstract BuilderTask createTask(BlockPos pos);

	public boolean shouldBeSkipped(BlockPos pos)
	{
		return false;
	}

	public boolean shouldBeDeferred(BlockPos pos)
	{
		return false;
	}

	@Nullable
	public BuilderTask getCurrentTask()
	{
		return this.currentTask;
	}

	protected void reset()
	{
		this.deferred.clear();
		this.currentTask = null;
		this.currentIndex = 0;
	}

	public boolean isDone()
	{
		return this.currentIndex >= this.indexer.getVolume() && this.deferred.isEmpty();
	}

	protected void advanceIndex()
	{
		while((this.currentIndex + 1) < this.indexer.getVolume())
		{
			++this.currentIndex;
			BlockPos pos = this.indexer.fromIndex(this.currentIndex);
			if(this.shouldBeSkipped(pos))
				continue;
			if(this.shouldBeDeferred(pos))
			{
				this.deferred.add(this.currentIndex);
				continue;
			}
			return;
		}
	}

	public void setWorld(@Nullable World world)
	{
		this.world = world;
	}

	@Override
	public void update()
	{
		if(this.isDone() || this.world == null)
			return;

		if(this.currentTask == null)
		{
			int taskIndex = this.currentIndex;
			if(taskIndex >= this.indexer.getVolume() && !this.deferred.isEmpty())
				taskIndex = this.deferred.removeInt(0);
			BlockPos pos = this.indexer.fromIndex(taskIndex);
			this.currentTask = this.createTask(pos);
		}

		if(!this.currentTask.canPerformTask())
			return;
		this.currentTask.performTask();
		this.currentTask = null;
		this.advanceIndex();
	}

	@Override
	public NBTTagCompound saveState()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(NBT_KEY_INDEX, this.currentIndex);
		tag.setIntArray(NBT_KEY_DEFERRED, this.deferred.toIntArray());
		return tag;
	}

	@Override
	public void restoreState(NBTTagCompound tag)
	{
		this.deferred.clear();
		this.deferred.addElements(0, tag.getIntArray(NBT_KEY_DEFERRED));
		this.currentIndex = tag.getInteger(NBT_KEY_INDEX);
	}
}
