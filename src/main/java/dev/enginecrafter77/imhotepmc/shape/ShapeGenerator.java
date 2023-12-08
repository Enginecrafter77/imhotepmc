package dev.enginecrafter77.imhotepmc.shape;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.util.math.BlockPos;

public interface ShapeGenerator {
	public ShapeGeneratorAction blockActionFor(BlockSelectionBox area, BlockPos pos);

	public static ShapeGenerator clear()
	{
		return (BlockSelectionBox area, BlockPos pos) -> ShapeGeneratorAction.CLEAR;
	}

	public static ShapeGenerator fill()
	{
		return (BlockSelectionBox area, BlockPos pos) -> ShapeGeneratorAction.PLACE;
	}

	public static enum ShapeGeneratorAction {PLACE, CLEAR, PASS}
}
