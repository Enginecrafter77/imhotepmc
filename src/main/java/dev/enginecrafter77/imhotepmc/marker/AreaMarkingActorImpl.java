package dev.enginecrafter77.imhotepmc.marker;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class AreaMarkingActorImpl implements AreaMarkingActor {
	@Nullable
	private BlockPos linkingPosition;

	public AreaMarkingActorImpl()
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
