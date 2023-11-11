package dev.enginecrafter77.imhotepmc.blueprint.iter;

import dev.enginecrafter77.imhotepmc.blueprint.ResolvedBlueprintBlock;
import net.minecraft.util.math.BlockPos;

public interface BlueprintVoxel {
	public BlockPos getPosition();

	public ResolvedBlueprintBlock getBlock();
}
