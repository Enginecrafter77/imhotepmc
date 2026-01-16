package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.BlockPos;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ImmutableBlueprintVoxel implements BlueprintVoxel {
	private final BlockPos pos;
	private final BlueprintEntry block;

	public ImmutableBlueprintVoxel(BlockPos pos, BlueprintEntry entry)
	{
		this.pos = pos.toImmutable();
		this.block = entry;
	}

	public ImmutableBlueprintVoxel(BlueprintVoxel copyFrom)
	{
		this(copyFrom.getPosition(), copyFrom.getBlueprintEntry());
	}

	@Override
	public BlueprintEntry getBlueprintEntry()
	{
		return this.block;
	}

	@Override
	public BlockPos getPosition()
	{
		return this.pos;
	}

	@Override
	public BlueprintVoxel withPosition(BlockPos position)
	{
		return new ImmutableBlueprintVoxel(position, this.block);
	}

	@Override
	public BlueprintVoxel withEntry(BlueprintEntry entry)
	{
		return new ImmutableBlueprintVoxel(this.pos, entry);
	}

	@Override
	public String toString()
	{
		return String.format("%s @ %s", this.block, this.pos);
	}

	public static ImmutableBlueprintVoxel copyOf(BlueprintVoxel voxel)
	{
		if(voxel instanceof ImmutableBlueprintVoxel)
			return (ImmutableBlueprintVoxel)voxel;
		return new ImmutableBlueprintVoxel(voxel);
	}
}
