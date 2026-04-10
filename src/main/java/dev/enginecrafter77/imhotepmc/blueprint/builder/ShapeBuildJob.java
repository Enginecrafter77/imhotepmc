package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.util.math.BlockPos;

public class ShapeBuildJob extends StructureBuildJob {
	private final ShapeGenerator generator;
	private final BlockSelectionBox area;
	private final ShapeBuildMode buildMode;

	public ShapeBuildJob(BuilderContext context, BlockSelectionBox area, ShapeGenerator generator, ShapeBuildMode buildMode)
	{
		super(context, area.getMinCorner(), area.getSize(), buildMode.createVoxelIndexer(area));
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

	@Override
	public BuilderTask createTask(BlockPos pos)
	{
		return this.buildMode.createShapeTask(this.context, pos);
	}

	@Override
	public TaskAction getTaskActionFor(BlockPos pos)
	{
		if(!this.generator.isBlockInShape(this.area, pos) || this.buildMode.wouldTaskBeInVain(this.getWorld(), pos))
			return TaskAction.SKIP;
		return super.getTaskActionFor(pos);
	}
}
