package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.BlockPos;

public class MutableBlueprintVoxel implements BlueprintVoxel {
	private final BlockPos.MutableBlockPos pos;
	private BlueprintEntry block;

	public MutableBlueprintVoxel()
	{
		this.pos = new BlockPos.MutableBlockPos();
		this.block = null;
	}

	public void set(BlockPos pos, BlueprintEntry block)
	{
		this.pos.setPos(pos);
		this.block = block;
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
		this.pos.setPos(position);
		return this;
	}

	@Override
	public BlueprintVoxel withEntry(BlueprintEntry entry)
	{
		this.block = entry;
		return this;
	}
}
