package dev.enginecrafter77.imhotepmc.blueprint.builder;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class DomeShapeGenerator extends FunctionalShapeGenerator {
	@Override
	public boolean isInShape(Point3d block, Point3d center, Tuple3d size)
	{
		double dcx = 4D * Math.pow(block.x - center.x, 2D) / Math.pow(size.x, 2D);
		double dcz = 4D * Math.pow(block.z - center.z, 2D) / Math.pow(size.z, 2D);
		double dcy = Math.pow(block.y - center.y + size.y/2D, 2D) / Math.pow(size.y, 2D);
		double dome = dcx + dcy + dcz;
		return dome <= 1D;
	}
}
