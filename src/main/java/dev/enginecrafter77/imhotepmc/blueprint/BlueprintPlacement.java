package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Set;

public class BlueprintPlacement implements Blueprint {
	private final Blueprint blueprint;
	private final BlockPos placementOrigin;
	private final Rotation rotation;
	private final Rotation complementRotation;
	private final Vec3i rotatedSize;

	private final BlockPos naturalOrigin;
	private final Vec3i naturalSize;

	public BlueprintPlacement(Blueprint blueprint, BlockPos placementOrigin, Rotation rotation)
	{
		this.blueprint = blueprint;
		this.rotation = rotation;

		this.complementRotation = getRotationComplement(rotation);

		this.placementOrigin = placementOrigin;
		this.rotatedSize = this.rotateBlockPosition(new BlockPos(blueprint.getSize()));

		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartSize(this.placementOrigin, this.rotatedSize);

		this.naturalOrigin = box.getMinCorner();
		this.naturalSize = box.getSize();
	}

	public BlueprintPlacement withOrigin(BlockPos origin)
	{
		return new BlueprintPlacement(this.blueprint, origin, this.rotation);
	}

	public BlueprintPlacement withRotation(Rotation rotation)
	{
		return new BlueprintPlacement(this.blueprint, this.placementOrigin, rotation);
	}

	public BlueprintPlacement withFacing(EnumFacing facing)
	{
		return this.withRotation(getRotationFromFacing(facing));
	}

	public Rotation getRotation()
	{
		return this.rotation;
	}

	public Blueprint getBlueprint()
	{
		return this.blueprint;
	}

	public BlockPos rotateBlockPosition(BlockPos blueprintPosition)
	{
		return blueprintPosition.rotate(this.rotation);
	}

	public BlockPos translateBlockPosition(BlockPos position)
	{
		return position.add(this.placementOrigin);
	}

	public BlueprintVoxel mapBlueprintVoxel(BlueprintVoxel voxel)
	{
		BlockPos position = voxel.getPosition();
		position = this.rotateBlockPosition(position);
		position = this.translateBlockPosition(position);
		return voxel.withPosition(position);
	}

	@Override
	public BlockPos getOriginOffset()
	{
		return this.naturalOrigin;
	}

	@Override
	public Vec3i getSize()
	{
		return this.naturalSize;
	}

	@Override
	public BlueprintEntry getBlockAt(BlockPos position)
	{
		position = position.subtract(this.placementOrigin);
		position = position.rotate(this.complementRotation);
		return this.blueprint.getBlockAt(position);
	}

	@Override
	public Set<? extends BlueprintEntry> palette()
	{
		return this.blueprint.palette();
	}

	@Override
	public int getDefinedBlockCount()
	{
		return this.blueprint.getDefinedBlockCount();
	}

	@Override
	public BlueprintReader reader()
	{
		return new BlueprintPlacementReader();
	}

	public static BlueprintPlacement facing(Blueprint blueprint, BlockPos origin, EnumFacing facing)
	{
		return new BlueprintPlacement(blueprint, origin, getRotationFromFacing(facing));
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

	private static Rotation getRotationComplement(Rotation rotation)
	{
		switch(rotation)
		{
		case CLOCKWISE_90:
			return Rotation.COUNTERCLOCKWISE_90;
		case COUNTERCLOCKWISE_90:
			return Rotation.CLOCKWISE_90;
		default:
			return rotation;
		}
	}

	private class BlueprintPlacementReader implements BlueprintReader
	{
		public final BlueprintReader wrapped;

		public BlueprintPlacementReader()
		{
			this.wrapped = BlueprintPlacement.this.blueprint.reader();
		}

		@Override
		public NBTTagCompound saveReaderState()
		{
			return this.wrapped.saveReaderState();
		}

		@Override
		public void restoreReaderState(NBTTagCompound tag)
		{
			this.wrapped.restoreReaderState(tag);
		}

		@Override
		public boolean hasNext()
		{
			return this.wrapped.hasNext();
		}

		@Override
		public BlueprintVoxel next()
		{
			return BlueprintPlacement.this.mapBlueprintVoxel(this.wrapped.next());
		}
	}
}
