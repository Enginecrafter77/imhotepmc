package dev.enginecrafter77.imhotepmc.cap;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class AreaMarkingEntityImpl implements AreaMarkingEntity {
	@Nullable
	private BlockPos linkingPosition;

	public AreaMarkingEntityImpl()
	{
		this.linkingPosition = null;
	}

	@Nullable
	@Override
	public BlockPos getCurrentLinkingPosition()
	{
		return this.linkingPosition;
	}

	@Override
	public void setCurrentLinkingPosition(@Nullable BlockPos pos)
	{
		this.linkingPosition = pos;
	}
}
