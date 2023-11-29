package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class BlueprintBuilder {
	private static final String NBT_KEY_DEFERRED = "deferred";

	private static final Vec3i VEC_ONE = new Vec3i(1, 1, 1);

	private final LinkedList<BlueprintVoxel> deferred;
	private final VoxelIndexer indexer;

	private final SchematicBlueprint blueprint;

	private Rotation rotation;

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
		this.rotation = Rotation.NONE;
		this.blueprint = blueprint;
		this.reader = this.blueprint.reader();
	}

	public SchematicBlueprint getBlueprint()
	{
		return this.blueprint;
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

	public void setRotation(Rotation rotation)
	{
		this.rotation = rotation;
	}

	public Rotation getRotation()
	{
		return this.rotation;
	}

	public void setRotationFromFacing(EnumFacing facing)
	{
		this.setRotation(getRotationFromFacing(facing));
	}

	public void reset()
	{
		this.reader = this.blueprint.reader();
	}

	public boolean hasNextBlock()
	{
		return this.reader.hasNext() || !this.deferred.isEmpty();
	}

	@Nullable
	protected BlueprintVoxel getNextVoxel()
	{
		if(this.world == null || this.origin == null)
			throw new IllegalStateException("World or origin not defined!");

		if(this.reader.hasNext())
		{
			BlueprintVoxel voxel = this.reader.next();
			BlockPos pos = voxel.getPosition().add(this.origin);
			Block blk = voxel.getBlock();
			if(blk == null)
				return null;
			if(!blk.canPlaceBlockAt(this.world, pos))
			{
				this.deferred.add(ImmutableBlueprintVoxel.copyOf(voxel));
				return null;
			}
			return voxel;
		}

		if(!this.deferred.isEmpty())
			return this.deferred.removeLast();

		throw new NoSuchElementException();
	}

	public Vec3i getBuildSize()
	{
		BlockPos max = new BlockPos(this.blueprint.getSize()).subtract(VEC_ONE);
		max = transformBlockPosition(max);
		max = max.add(VEC_ONE);
		return max;
	}

	protected BlockPos transformBlockPosition(BlockPos blueprintPosition)
	{
		Vec3i unitShift = new Vec3i(this.blueprint.getSize().getX() / 2, this.blueprint.getSize().getY() / 2, this.blueprint.getSize().getZ() / 2);
		BlockPos unitPosition = blueprintPosition.subtract(unitShift);

		unitPosition = unitPosition.rotate(this.rotation);
		unitShift = new BlockPos(unitShift).rotate(this.rotation);

		return unitPosition.add(unitShift);
	}

	protected BlockPos transformBlockPositionToFinal(BlockPos blueprintPosition)
	{
		blueprintPosition = this.transformBlockPosition(blueprintPosition);
		if(this.origin != null)
			blueprintPosition = blueprintPosition.add(this.origin);
		return blueprintPosition;
	}

	public void placeNextBlock()
	{
		BlueprintVoxel voxel = null;
		while(voxel == null && this.hasNextBlock())
			voxel = this.getNextVoxel();
		if(voxel == null)
			return;

		if(this.world == null || this.origin == null)
			throw new IllegalStateException("World or origin not defined!");

		IBlockState state = voxel.createBlockState();
		if(state == null)
			return;

		BlockPos dest = this.transformBlockPositionToFinal(voxel.getPosition());
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
			this.deferred.add(new ImmutableBlueprintVoxel(pos, entry));
		}
	}

	private static Rotation getRotationFromFacing(EnumFacing facing)
	{
		switch(facing.getOpposite())
		{
		default:
		case NORTH:
			return Rotation.NONE;
		case SOUTH:
			return Rotation.CLOCKWISE_180;
		case WEST:
			return Rotation.COUNTERCLOCKWISE_90;
		case EAST:
			return Rotation.CLOCKWISE_90;
		}
	}
}
