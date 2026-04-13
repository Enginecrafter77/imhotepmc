package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import net.minecraft.util.math.BlockPos;

public interface ShapeGenerator {
	public static final ShapeGenerator ALL = (Box3i area, BlockPos pos) -> true;
	public static final ShapeGenerator NONE = (Box3i area, BlockPos pos) -> false;

	public boolean isBlockInShape(Box3i area, BlockPos pos);
}
