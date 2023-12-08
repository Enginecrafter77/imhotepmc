package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.shape.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum TerraformMode {
	CLEAR(ShapeGenerator.clear(), ShapeBuildStrategy.TOP_DOWN),
	FILL(ShapeGenerator.fill(), ShapeBuildStrategy.BOTTOM_UP),
	ELLIPSOID(new EllipsoidShapeGenerator(), ShapeBuildStrategy.BOTTOM_UP),
	PYRAMID(new PyramidShapeGenerator(), ShapeBuildStrategy.BOTTOM_UP),
	DOME(new DomeShapeGenerator(), ShapeBuildStrategy.BOTTOM_UP);

	private final ShapeGenerator shapeGenerator;
	private final ShapeBuildStrategy buildStrategy;

	private TerraformMode(ShapeGenerator generator, ShapeBuildStrategy strategy)
	{
		this.shapeGenerator = generator;
		this.buildStrategy = strategy;
	}

	public ShapeGenerator getShapeGenerator()
	{
		return this.shapeGenerator;
	}

	public ShapeBuildStrategy getBuildStrategy()
	{
		return this.buildStrategy;
	}

	public ITextComponent getTranslatedName()
	{
		return new TextComponentTranslation(String.format("shape.%s.name", this.name().toLowerCase()));
	}
}
