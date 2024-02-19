package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ShapeBuildJob extends StructureBuildJob {
	private final ShapeGenerator generator;
	private final BlockSelectionBox area;
	private final ShapeBuildMode buildMode;
	private final BuilderContext context;

	public ShapeBuildJob(BlockSelectionBox area, ShapeGenerator generator, ShapeBuildMode buildMode, BuilderContext context)
	{
		super(area.getMinCorner(), area.getSize(), buildMode.createVoxelIndexer(area));
		this.context = context;
		this.generator = generator;
		this.buildMode = buildMode;
		this.area = area;
	}

	public ShapeBuildMode getBuildMode()
	{
		return this.buildMode;
	}

	public ShapeGenerator getGenerator()
	{
		return this.generator;
	}

	@Nonnull
	@Override
	public BuilderTask createTask(BlockPos pos)
	{
		return this.buildMode.createShapeTask(Objects.requireNonNull(this.world), pos, this.context);
	}

	@Override
	public boolean shouldBeSkipped(BlockPos pos)
	{
		return !this.generator.isBlockInShape(this.area, pos) || this.buildMode.wouldTaskBeInVain(Objects.requireNonNull(this.world), pos);
	}
}
