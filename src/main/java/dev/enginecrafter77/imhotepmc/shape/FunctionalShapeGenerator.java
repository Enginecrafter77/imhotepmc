package dev.enginecrafter77.imhotepmc.shape;

import dev.enginecrafter77.imhotepmc.util.BlockAnchor;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import dev.enginecrafter77.imhotepmc.util.Box3d;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public abstract class FunctionalShapeGenerator implements ShapeGenerator {
	private final Box3d box;
	private final Point3d center;
	private final Point3d block;
	private final Tuple3d size;

	public FunctionalShapeGenerator()
	{
		this.box = new Box3d();
		this.center = new Point3d();
		this.block = new Point3d();
		this.size = new Vector3d();
	}

	public abstract boolean isInShape(Point3d block, Point3d center, Tuple3d size);

	public boolean isBlockInShape(BlockSelectionBox area, BlockPos pos)
	{
		this.box.set(area);
		this.box.getCenter(this.center);
		this.box.getSize(this.size);
		BlockAnchor.CENTER.anchorToBlock(pos, this.block);
		return this.isInShape(this.block, this.center, this.size);
	}
}
