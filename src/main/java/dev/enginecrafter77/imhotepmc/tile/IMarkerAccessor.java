package dev.enginecrafter77.imhotepmc.tile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public interface IMarkerAccessor {
	@Nullable
	public IAreaMarker getMarker(IBlockAccess world, BlockPos pos);
}
