package dev.enginecrafter77.imhotepmc.tile;

import net.minecraft.util.math.BlockPos;

public interface IAreaMarker {
	public BlockPos getMarkerPosition();
	public AreaMarkGroup getCurrentMarkGroup();
	public void setMarkGroup(AreaMarkGroup group);
}
