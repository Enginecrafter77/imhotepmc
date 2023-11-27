package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class BlueprintBuilder {
	private static final String NBT_KEY_DEFERRED = "deferred";

	private final LinkedList<BlueprintVoxel> deferred;
	private final VoxelIndexer indexer;

	private final SchematicBlueprint blueprint;

	@Nonnull
	private BlueprintReader reader;

	@Nullable
	private BlockPos origin;
	@Nullable
	private World world;

	public BlueprintBuilder(SchematicBlueprint blueprint)
	{
		this.indexer = new NaturalVoxelIndexer(blueprint.getSize());
		this.deferred = new LinkedList<BlueprintVoxel>();
		this.blueprint = blueprint;
		this.reader = this.blueprint.reader();
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	@Nullable
	public World getWorld()
	{
		return this.world;
	}

	public void setOrigin(BlockPos origin)
	{
		this.origin = origin;
	}

	@Nullable
	public BlockPos getOrigin()
	{
		return this.origin;
	}

	public void reset()
	{
		this.reader = this.blueprint.reader();
	}

	public boolean hasNextBlock()
	{
		return this.reader.hasNext() || !this.deferred.isEmpty();
	}

	public void placeNextBlock()
	{
		if(this.world == null || this.origin == null)
			throw new IllegalStateException("World or origin not defined!");

		if(this.reader.hasNext())
		{
			BlueprintVoxel voxel = this.reader.next();
			BlockPos pos = voxel.getPosition().add(this.origin);
			Block blk = voxel.getBlock();
			if(blk == null)
				return;
			if(!blk.canPlaceBlockAt(this.world, pos))
			{
				this.deferred.add(ImmutableBlueprintVoxel.copyOf(voxel));
				return;
			}
			this.placeTile(voxel);
			return;
		}

		if(!this.deferred.isEmpty())
		{
			BlueprintVoxel deferredVoxel = this.deferred.removeLast();
			this.placeTile(deferredVoxel);
			return;
		}

		throw new NoSuchElementException();
	}

	private void placeTile(BlueprintVoxel voxel)
	{
		if(this.world == null || this.origin == null)
			throw new IllegalStateException("World or origin not defined!");

		IBlockState state = voxel.createBlockState();
		if(state == null)
			return;

		BlockPos dest = voxel.getPosition().add(this.origin);
		this.world.setBlockState(dest, state, 2);
		TileEntity tile = voxel.createTileEntity(this.world);
		if(tile != null)
			this.world.setTileEntity(dest, tile);
		this.world.scheduleBlockUpdate(dest, state.getBlock(), 100, 1);
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
			BlueprintEntry entry = this.blueprint.getBlockAt(pos);
			if(entry == null)
				continue;
			this.deferred.add(new ImmutableBlueprintVoxel(pos, entry));
		}
	}
}
