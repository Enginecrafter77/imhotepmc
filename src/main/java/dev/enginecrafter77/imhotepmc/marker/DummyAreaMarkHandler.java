package dev.enginecrafter77.imhotepmc.marker;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class DummyAreaMarkHandler extends AbstractAreaMarkHandler {
	private final Map<BlockPos, DummyAnchor> map;

	public DummyAreaMarkHandler()
	{
		this.map = new TreeMap<>();
	}

	@Nullable
	@Override
	public MarkingAnchor getAnchorAt(BlockPos pos)
	{
		return this.map.computeIfAbsent(pos, DummyAnchor::new);
	}

	private static class DummyAnchor implements MarkingAnchor
	{
		private final BlockPos pos;

		@Nullable
		private UUID id;

		public DummyAnchor(BlockPos pos)
		{
			this.pos = pos;
			this.id = null;
		}

		@Override
		public BlockPos getMarkerPosition()
		{
			return this.pos;
		}

		@Nullable
		@Override
		public UUID getAreaId()
		{
			return this.id;
		}

		@Override
		public void setAreaId(@Nullable UUID id)
		{
			this.id = id;
		}

		@Override
		public void dismantle()
		{
			//NOOP
		}
	}
}
