package dev.enginecrafter77.imhotepmc.tile;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IAreaMarker {
	public BlockPos getMarkerPosition();

	@Nullable
	public AreaMarkGroup getCurrentMarkGroup();
	public void setMarkGroup(@Nullable AreaMarkGroup group);
}
