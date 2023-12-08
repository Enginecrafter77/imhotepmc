package dev.enginecrafter77.imhotepmc.blueprint.shape;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.util.math.BlockPos;

public interface ShapeGenerator {
	public static final ShapeGenerator ALL = (BlockSelectionBox area, BlockPos pos) -> true;
	public static final ShapeGenerator NONE = (BlockSelectionBox area, BlockPos pos) -> false;

	public boolean isBlockInShape(BlockSelectionBox area, BlockPos pos);
}
