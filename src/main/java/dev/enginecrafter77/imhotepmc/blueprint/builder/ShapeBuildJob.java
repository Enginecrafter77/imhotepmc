package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class ShapeBuildJob extends StructureBuildJob {
	private final ShapeGenerator generator;
	private final Box3i area;
	private final ShapeBuildMode buildMode;

	public ShapeBuildJob(BuilderContext context, Box3i area, ShapeGenerator generator, ShapeBuildMode buildMode)
	{
		super(context, boxOrigin(area), boxSize(area), buildMode.createVoxelIndexer(boxOrigin(area), boxSize(area)));
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

	private static BlockPos boxOrigin(Box3i box)
	{
		return new BlockPos(box.start.x, box.start.y, box.start.z);
	}

	private static Vec3i boxSize(Box3i box)
	{
		return new Vec3i(box.getSizeX(), box.getSizeY(), box.getSizeZ());
	}
}
