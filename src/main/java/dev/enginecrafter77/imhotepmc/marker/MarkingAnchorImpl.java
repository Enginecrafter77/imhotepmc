package dev.enginecrafter77.imhotepmc.marker;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.UUID;

public class MarkingAnchorImpl implements MarkingAnchor {
	@Nullable
	private Runnable onDismantle;

	@Nullable
	private BlockPos pos;

	@Nullable
	private UUID areaId;

	public MarkingAnchorImpl()
	{
		this.onDismantle = null;
		this.areaId = null;
		this.pos = null;
	}

	public void setPos(BlockPos pos)
	{
		this.pos = pos;
	}

	public void setOnDismantleAction(Runnable action)
	{
		this.onDismantle = action;
	}

	@Override
	public BlockPos getMarkerPosition()
	{
		if(this.pos == null)
			throw new NoSuchElementException();
		return this.pos;
	}

	@Nullable
	@Override
	public UUID getAreaId()
	{
		return this.areaId;
	}

	@Override
	public void setAreaId(@Nullable UUID id)
	{
		this.areaId = id;
	}

	@Override
	public void dismantle()
	{
		if(this.onDismantle != null)
			this.onDismantle.run();
	}
}
