package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.BlockPos;

public interface BlueprintVoxel {
	public BlueprintEntry getBlueprintEntry();
	public BlockPos getPosition();

	public BlueprintVoxel withPosition(BlockPos position);
	public BlueprintVoxel withEntry(BlueprintEntry entry);
}
