package dev.enginecrafter77.imhotepmc.marker;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface AreaMarkingActor {
	@Nullable
	public BlockPos getCurrentLinkingPosition();

	public void setCurrentLinkingPosition(@Nullable BlockPos pos);
}
