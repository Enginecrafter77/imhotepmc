package dev.enginecrafter77.imhotepmc.radar;

import dev.enginecrafter77.imhotepmc.util.FastBlockPosSet;
import net.minecraft.util.math.BlockPos;

public interface RadarHandler {
	public FastBlockPosSet ping(BlockPos source);
}
