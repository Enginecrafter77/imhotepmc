package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BlueprintBuilder {
	private static final String NBT_KEY_DEFERRED = "deferred";

	private final LinkedList<BlueprintVoxel> deferred;
	private final VoxelIndexer indexer;
	private final BlueprintPlacement placement;

	@Nonnull
	private BlueprintReader reader;

	public BlueprintBuilder(BlueprintPlacement placement)
	{
		this.indexer = new NaturalVoxelIndexer(placement.getOriginOffset(), placement.getSize());
		this.deferred = new LinkedList<BlueprintVoxel>();
		this.placement = placement;
		this.reader = placement.reader();
	}

	public BlueprintPlacement getPlacement()
	{
		return this.placement;
	}

	public void reset()
	{
		this.reader = this.placement.reader();
		this.deferred.clear();
	}

	public boolean hasNextBlock()
	{
		return this.reader.hasNext() || !this.deferred.isEmpty();
	}

	protected int getBlockDeferScore(World world, Block blk, BlockPos pos)
	{
		int score = 0;

		if(blk instanceof BlockFalling)
			score += 1;

		if(!blk.canPlaceBlockAt(world, pos))
			score += 10;

		return score;
	}

	public void deferVoxel(World world, BlueprintVoxel voxel)
	{
		Comparator<BlueprintVoxel> cmp = Comparator.comparing((BlueprintVoxel vx) -> this.getBlockDeferScore(world, Objects.requireNonNull(vx.getBlueprintEntry().getBlock()), vx.getPosition()));
		int index = Collections.binarySearch(this.deferred, voxel, cmp);
		if(index < 0)
			index = -index - 1;
		this.deferred.add(index, voxel);
	}

	@Nullable
	protected BlueprintVoxel getNextVoxel(World world)
	{
		if(this.reader.hasNext())
		{
			BlueprintVoxel voxel = this.reader.next();
			Block blk = voxel.getBlueprintEntry().getBlock();
			if(blk == null)
				return null;
			int deferScore = this.getBlockDeferScore(world, blk, voxel.getPosition());
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

	public void placeNextBlock(World world)
	{
		BlueprintVoxel voxel = null;
		while(voxel == null && this.hasNextBlock())
			voxel = this.getNextVoxel(world);
		if(voxel == null)
			return;

		IBlockState state = voxel.getBlueprintEntry().createBlockState();
		if(state == null)
			return;
		state = state.withRotation(this.placement.getRotation());

		BlockPos dest = voxel.getPosition();
		world.setBlockState(dest, state, 2);
		TileEntity tile = voxel.getBlueprintEntry().createTileEntity(world);
		if(tile != null)
			world.setTileEntity(dest, tile);
		world.scheduleBlockUpdate(dest, state.getBlock(), 100, 1);
	}

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
