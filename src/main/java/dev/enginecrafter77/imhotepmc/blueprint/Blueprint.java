package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import java.util.Set;

public interface Blueprint {
	public Vec3i getSize();

	public BlockPos getOriginOffset();

	public BlueprintEntry getBlockAt(BlockPos position);

	public Set<? extends BlueprintEntry> palette();

	public int getDefinedBlockCount();

	@Nonnull
	public BlueprintReader reader();
}
