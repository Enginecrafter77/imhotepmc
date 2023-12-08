package dev.enginecrafter77.imhotepmc.blueprint.shape;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class EllipsoidShapeGenerator extends FunctionalShapeGenerator {
	@Override
	public boolean isInShape(Point3d block, Point3d center, Tuple3d size)
	{
		double px = Math.pow(block.x - center.x, 2D) / Math.pow(size.x, 2D);
		double py = Math.pow(block.y - center.y, 2D) / Math.pow(size.y, 2D);
		double pz = Math.pow(block.z - center.z, 2D) / Math.pow(size.z, 2D);
		double ellipsoid = px + py + pz;
		return ellipsoid < 0.25D;
	}
}
