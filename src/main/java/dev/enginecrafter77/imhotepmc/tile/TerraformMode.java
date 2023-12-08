package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.shape.ShapeBuildStrategy;
import dev.enginecrafter77.imhotepmc.shape.ShapeGenerator;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum TerraformMode {
	CLEAR(ShapeGenerator.clear(), ShapeBuildStrategy.TOP_DOWN),
	FILL(ShapeGenerator.fill(), ShapeBuildStrategy.BOTTOM_UP);

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
