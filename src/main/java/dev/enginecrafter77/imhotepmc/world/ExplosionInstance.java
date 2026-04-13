package dev.enginecrafter77.imhotepmc.world;

import dev.enginecrafter77.imhotepmc.blueprint.builder.EllipsoidShapeGenerator;
import dev.enginecrafter77.imhotepmc.blueprint.builder.ShapeGenerator;
import dev.enginecrafter77.imhotepmc.util.ShapedBlockPosIterator;
import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import net.minecraft.util.math.BlockPos;

public class ExplosionInstance {
	public final BlockPos epicenter;
	public final double radius;

	public ExplosionInstance(BlockPos epicenter, double radius)
	{
		this.epicenter = epicenter;
		this.radius = radius;
	}

	public Box3i getAffectedArea()
	{
		Box3i box = new Box3i();
		box.set(
				(int)Math.floor(this.epicenter.getX() - this.radius/2),
				(int)Math.floor(this.epicenter.getY() - this.radius/2),
				(int)Math.floor(this.epicenter.getZ() - this.radius/2),
				(int)Math.ceil(this.epicenter.getX() + this.radius/2),
				(int)Math.ceil(this.epicenter.getY() + this.radius/2),
				(int)Math.ceil(this.epicenter.getZ() + this.radius/2)
		);
		return box;
	}

	public Iterable<BlockPos.MutableBlockPos> getExplodedBlocks()
	{
		ShapeGenerator shape = new EllipsoidShapeGenerator();
		return ShapedBlockPosIterator.asIterable(this.getAffectedArea(), shape);
	}
}
