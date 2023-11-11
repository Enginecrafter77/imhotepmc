package dev.enginecrafter77.imhotepmc.blueprint.iter;

import dev.enginecrafter77.imhotepmc.blueprint.ResolvedBlueprintBlock;
import net.minecraft.util.math.BlockPos;

public class MutableBlueprintVoxel implements BlueprintVoxel {
	private final BlockPos.MutableBlockPos pos;
	private ResolvedBlueprintBlock block;

	public MutableBlueprintVoxel()
	{
		this.pos = new BlockPos.MutableBlockPos();
		this.block = null;
	}

	public void set(BlockPos pos, ResolvedBlueprintBlock block)
	{
		this.pos.setPos(pos);
		this.block = block;
	}

	@Override
	public BlockPos getPosition()
	{
		return this.pos;
	}

	@Override
	public ResolvedBlueprintBlock getBlock()
	{
		return this.block;
	}
}
