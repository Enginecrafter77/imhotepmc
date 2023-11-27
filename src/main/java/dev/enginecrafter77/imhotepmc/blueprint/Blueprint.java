package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Blueprint {
	public Vec3i getSize();

	public BlockPos getOriginOffset();

	@Nullable
	public BlueprintEntry getBlockAt(BlockPos position);

	public int getDefinedBlockCount();

	@Nonnull
	public BlueprintReader reader();
}
