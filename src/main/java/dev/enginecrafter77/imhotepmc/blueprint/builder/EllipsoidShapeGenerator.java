package dev.enginecrafter77.imhotepmc.blueprint.builder;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class EllipsoidShapeGenerator extends FunctionalShapeGenerator {
	@Override
	public boolean isInShape(Point3d block, Point3d center, Tuple3d size)
	{
		// ((x-cx)/0.5vx)^2 + ((y-cy)/0.5vy)^2 + ((z-cz)/0.5vz)^2 < 1
		// => Factor out /0.5 as *2, square to 4, extract before parentheses and divide the whole equation by 4
		// => ((x-cx)/vx)^2 + ((y-cy)/vy)^2 + ((z-cz)/vz)^2 < 1/4
		double px = Math.pow((block.x - center.x) / size.x, 2D);
		double py = Math.pow((block.y - center.y) / size.y, 2D);
		double pz = Math.pow((block.z - center.z) / size.z, 2D);
		double ellipsoid = px + py + pz;
		return ellipsoid < 0.25D;
	}
}
