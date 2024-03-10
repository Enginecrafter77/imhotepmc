package dev.enginecrafter77.imhotepmc.world;

import dev.enginecrafter77.imhotepmc.blueprint.builder.EllipsoidShapeGenerator;
import dev.enginecrafter77.imhotepmc.blueprint.builder.ShapeGenerator;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import dev.enginecrafter77.imhotepmc.util.ShapedBlockPosIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class ExplosionInstance {
	public final BlockPos epicenter;
	public final double radius;

	public ExplosionInstance(BlockPos epicenter, double radius)
	{
		this.epicenter = epicenter;
		this.radius = radius;
	}

	public BlockSelectionBox getAffectedArea()
	{
		Vec3i offset = new Vec3i(this.radius, this.radius, this.radius);
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartEnd(this.epicenter.subtract(offset), this.epicenter.add(offset));
		return box;
	}

	public Iterable<BlockPos.MutableBlockPos> getExplodedBlocks()
	{
		BlockSelectionBox box = this.getAffectedArea();
		ShapeGenerator shape = new EllipsoidShapeGenerator();
		return ShapedBlockPosIterator.asIterable(box, shape);
	}
}
