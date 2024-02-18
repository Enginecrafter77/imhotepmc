package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class BlueprintBuilder implements StructureBuilder {
	private static final String NBT_KEY_DEFERRED = "deferred";

	private final LinkedList<BlueprintVoxel> deferred;
	private final BuilderContext context;
	private final VoxelIndexer indexer;
	private final BlueprintPlacement placement;
	private final BlueprintReader reader;

	@Nullable
	private BuilderTask currentTask;

	public BlueprintBuilder(BlueprintPlacement placement, BuilderContext context)
	{
		this.indexer = new NaturalVoxelIndexer(placement.getOriginOffset(), placement.getSize());
		this.deferred = new LinkedList<BlueprintVoxel>();
		this.placement = placement;
		this.context = context;
		this.reader = placement.reader();
		this.currentTask = null;
	}

	public BlueprintPlacement getPlacement()
	{
		return this.placement;
	}

	@Override
	public boolean nextTask(World world)
	{
		this.currentTask = null;
		while(this.hasNextVoxel())
		{
			BlueprintVoxel voxel = this.getNextVoxel(world);
			if(voxel == null)
				continue;
			BlueprintEntry entry = voxel.getBlueprintEntry();
			Block blk = entry.getBlock();
			if(blk == null)
				continue;
			BuilderBlockPlacementDetails details = BuilderBlockPlacementDetails.fromBlueprintEntry(entry).rotated(this.placement.getRotation());
			this.currentTask = new BuilderPlaceTask(world, voxel.getPosition(), details, this.context);
			return true;
		}

		return this.currentTask != null;
	}

	@Nullable
	@Override
	public BuilderTask getLastTask(World world)
	{
		return this.currentTask;
	}

	protected int getVoxelDeferScore(World world, BlueprintVoxel voxel)
	{
		BlockPos pos = voxel.getPosition();
		IBlockState state = voxel.getBlueprintEntry().createBlockState();
		if(state == null)
			return 0;
		Block blk = state.getBlock();

		int score = 0;

		if(blk instanceof BlockFalling)
			score += 1;

		if(!state.isFullCube())
			score += 3;

		if(!state.isFullBlock())
			score += 4;

		if(!blk.canPlaceBlockAt(world, pos))
			score += 100;

		return score;
	}

	public void deferVoxel(World world, BlueprintVoxel voxel)
	{
		Comparator<BlueprintVoxel> cmp = Comparator.comparing((BlueprintVoxel vx) -> this.getVoxelDeferScore(world, vx));
		int index = Collections.binarySearch(this.deferred, voxel, cmp);
		if(index < 0)
			index = -index - 1;
		this.deferred.add(index, ImmutableBlueprintVoxel.copyOf(voxel));
	}

	protected boolean hasNextVoxel()
	{
		return !this.deferred.isEmpty() || this.reader.hasNext();
	}

	@Nullable
	protected BlueprintVoxel getNextVoxel(World world)
	{
		if(this.reader.hasNext())
		{
			BlueprintVoxel voxel = this.reader.next();
			Block blk = voxel.getBlueprintEntry().getBlock();
			if(blk == null || blk == Blocks.AIR)
				return null;

			IBlockState current = world.getBlockState(voxel.getPosition());
			if(current.getBlock() == blk)
				return null;

			int deferScore = this.getVoxelDeferScore(world, voxel);
			if(deferScore > 0)
			{
				this.deferVoxel(world, voxel);
				return null;
			}
			return voxel;
		}

		if(!this.deferred.isEmpty())
			return this.deferred.removeLast();

		throw new NoSuchElementException();
	}

	@Override
	public NBTTagCompound saveState()
	{
		NBTTagCompound tag = this.reader.saveReaderState();

		int[] deferredIndices = new int[this.deferred.size()];
		int ii = 0;
		for(BlueprintVoxel voxel : this.deferred)
			deferredIndices[ii++] = this.indexer.toIndex(voxel.getPosition());
		tag.setIntArray(NBT_KEY_DEFERRED, deferredIndices);

		return tag;
	}

	@Override
	public void restoreState(NBTTagCompound tag)
	{
		this.reader.restoreReaderState(tag);

		this.deferred.clear();
		int[] deferredIndices = tag.getIntArray(NBT_KEY_DEFERRED);
		for(int deferredIndex : deferredIndices)
		{
			BlockPos pos = this.indexer.fromIndex(deferredIndex);
			BlueprintEntry entry = this.placement.getBlockAt(pos);
			this.deferred.add(new ImmutableBlueprintVoxel(pos, entry));
		}
	}
}
