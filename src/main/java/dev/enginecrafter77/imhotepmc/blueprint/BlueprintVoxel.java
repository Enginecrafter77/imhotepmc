package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import net.minecraft.util.math.BlockPos;

public interface BlueprintVoxel extends BlueprintEntry {
	public BlockPos getPosition();
}
