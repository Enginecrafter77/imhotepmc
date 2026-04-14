package dev.enginecrafter77.imhotepmc.marker;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.UUID;

public interface MarkingAnchor {
	public BlockPos getMarkerPosition();
	@Nullable
	public UUID getAreaId();
	public void setAreaId(@Nullable UUID id);
	public void dismantle();
}
