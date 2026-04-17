package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.NaturalVoxelIndexer;
import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import dev.enginecrafter77.imhotepmc.util.SaveableStateHolder;
import dev.enginecrafter77.imhotepmc.util.transaction.Transaction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class StructureBuildJob implements SaveableStateHolder<NBTTagCompound>, ITickable {
	private static final String NBT_KEY_DEFERRED = "deferred";
	private static final String NBT_KEY_INDEX = "index";

	protected final BuilderContext context;
	protected final BlockPos buildOrigin;
	protected final Vec3i buildSize;

	private final IntList deferred;
	private final VoxelIndexer indexer;

	@Nullable
	private BuilderTask currentTask;
	private boolean advanceTaskNextRound;
	private int currentIndex;

	public StructureBuildJob(BuilderContext context, BlockPos buildOrigin, Vec3i buildSize, VoxelIndexer indexer)
	{
		this.deferred = new IntArrayList();
		this.buildOrigin = buildOrigin;
		this.buildSize = buildSize;
		this.indexer = indexer;
		this.context = context;
		this.advanceTaskNextRound = true;
		this.currentTask = null;
		this.currentIndex = -1;
	}

	public StructureBuildJob(BuilderContext context, BlockPos buildOrigin, Vec3i buildSize)
	{
		this(context, buildOrigin, buildSize, new NaturalVoxelIndexer(buildOrigin, buildSize));
	}

	public abstract BuilderTask createTask(BlockPos pos);

	public TaskAction getTaskActionFor(BlockPos pos)
	{
		return TaskAction.PROCEED;
	}

	@Nullable
	public BuilderTask getCurrentTask()
	{
		return this.currentTask;
	}

	protected World getWorld()
	{
		return this.context.getWorld();
	}

	protected void reset()
	{
		this.deferred.clear();
		this.currentTask = null;
		this.currentIndex = -1;
	}

	private int getIndexLimit()
	{
		return this.indexer.getVolume() + this.deferred.size();
	}

	public boolean isDone()
	{
		return (this.currentIndex + 1) >= this.getIndexLimit();
	}

	private boolean isProcessingDeferred()
	{
		return this.currentIndex >= this.indexer.getVolume();
	}

	/**
	 * Computes a real index that falls into the volume of the internal indexer.
	 * <ul>
	 *     <li>If the current index falls within the indexer's range, the current index is returned.</li>
	 *     <li>
	 *         If the current index is greater than or equal to the indexer's volume, then the index is assumed to be of a deferred block.
	 *         <ul>
	 *             <li>The position in the deferred array is then given as <code>i-V</code>, <code>i</code> being current index and <code>V</code> being indexer volume.</li>
	 *             <li>The deferred index is used to look up the real index inside the deferred items array</li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 * @return A real index falling into the volume of the internal indexer.
	 */
	private int getRealIndex()
	{
		if(!this.isProcessingDeferred())
			return this.currentIndex;
		int realIndex = this.currentIndex - this.indexer.getVolume();
		return this.deferred.getInt(realIndex);
	}

	protected void advanceIndex()
	{
		while((this.currentIndex + 1) < this.getIndexLimit())
		{
			++this.currentIndex;
			int realIndex = this.getRealIndex();
			BlockPos pos = this.indexer.fromIndex(realIndex);
			switch(this.getTaskActionFor(pos))
			{
			case DEFER:
				if(this.isProcessingDeferred())
					return; // refuse to defer anymore
				this.deferred.add(realIndex);
			case SKIP:
				continue;
			case PROCEED:
				return;
			}
		}
	}

	private BuilderTask createActiveTask()
	{
		BlockPos pos = this.indexer.fromIndex(this.getRealIndex());
		return this.createTask(pos);
	}

	@Override
	public void update()
	{
		if(this.isDone())
		{
			// cleanup deferred (to avoid storing it further in NBT)
			this.currentIndex = this.indexer.getVolume();
			this.deferred.clear();
			return;
		}

		if(this.advanceTaskNextRound)
		{
			this.advanceTaskNextRound = false;
			this.advanceIndex();
		}

		if(this.currentTask == null)
			this.currentTask = this.createActiveTask();
		this.currentTask.update();

		Transaction transaction = this.currentTask.asTransaction();
		if(!transaction.canCommit())
			return;
		transaction.commit();
		this.currentTask = null;
		this.advanceTaskNextRound = true;
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
		this.advanceTaskNextRound = false;
	}

	public static enum TaskAction {
		/** Proceed with executing action for given block */
		PROCEED,
		/** Skip executing task for placing this block. */
		SKIP,
		/** Skip executing the task for now, return to it later (i.e. place the task to the end of the queue) */
		DEFER
	}
}
