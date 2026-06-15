package dev.enginecrafter77.imhotepmc.radar;

import dev.enginecrafter77.imhotepmc.util.FastBlockPosSet;
import net.minecraft.util.math.BlockPos;

public class ShimRadarHandler implements RadarHandler {
	@Override
	public FastBlockPosSet ping(BlockPos source)
	{
		return new FastBlockPosSet();
	}
}
