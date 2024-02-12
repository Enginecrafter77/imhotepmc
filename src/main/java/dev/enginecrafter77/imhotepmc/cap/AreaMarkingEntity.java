package dev.enginecrafter77.imhotepmc.cap;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface AreaMarkingEntity {
	@Nullable
	public BlockPos getCurrentLinkingPosition();

	public void setCurrentLinkingPosition(@Nullable BlockPos pos);
}
