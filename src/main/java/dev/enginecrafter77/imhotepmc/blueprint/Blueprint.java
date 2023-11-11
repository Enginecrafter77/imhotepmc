package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.blueprint.iter.BlueprintIterator;
import dev.enginecrafter77.imhotepmc.blueprint.iter.BlueprintVoxel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public interface Blueprint extends Iterable<BlueprintVoxel> {
	@Nullable
	public ResolvedBlueprintBlock getBlockAt(BlockPos position);

	public int getBlockCount();

	public Vec3i getSize();

	public default int getTotalVolume()
	{
		Vec3i size = this.getSize();
		return size.getX() * size.getY() * size.getZ();
	}

	public default BlockPos getOrigin()
	{
		return BlockPos.ORIGIN;
	}

	@Nonnull
	public default Iterator<BlueprintVoxel> iterator()
	{
		return new BlueprintIterator(this);
	}
}
