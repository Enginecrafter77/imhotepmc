package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.shape.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum TerraformMode {
	CLEAR(ShapeGenerator.ALL, ShapeBuildMode.CLEAR),
	FILL(ShapeGenerator.ALL, ShapeBuildMode.BUILD),
	ELLIPSOID(new EllipsoidShapeGenerator(), ShapeBuildMode.BUILD),
	PYRAMID(new PyramidShapeGenerator(), ShapeBuildMode.BUILD),
	DOME(new DomeShapeGenerator(), ShapeBuildMode.BUILD);

	private final ShapeGenerator shapeGenerator;
	private final ShapeBuildMode buildStrategy;

	private TerraformMode(ShapeGenerator generator, ShapeBuildMode strategy)
	{
		this.shapeGenerator = generator;
		this.buildStrategy = strategy;
	}

	public ShapeGenerator getShapeGenerator()
	{
		return this.shapeGenerator;
	}

	public ShapeBuildMode getBuildStrategy()
	{
		return this.buildStrategy;
	}

	public ITextComponent getTranslatedName()
	{
		return new TextComponentTranslation(String.format("shape.%s.name", this.name().toLowerCase()));
	}
}
