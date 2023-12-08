package dev.enginecrafter77.imhotepmc.blueprint.shape;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class PyramidShapeGenerator extends FunctionalShapeGenerator {
	@Override
	public boolean isInShape(Point3d block, Point3d center, Tuple3d size)
	{
		double stx = (block.x - center.x) / size.x;
		double stz = (block.z - center.z) / size.z;
		double pyr = Math.abs(stx - stz) + Math.abs(stx + stz);
		pyr = (0.5D - pyr) * size.y + center.y;
		return block.y <= pyr;
	}
}
