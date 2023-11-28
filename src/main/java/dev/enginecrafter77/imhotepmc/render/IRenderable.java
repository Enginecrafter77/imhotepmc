package dev.enginecrafter77.imhotepmc.render;

import javax.vecmath.Tuple3d;

public interface IRenderable {
	public void doRender(double x, double y, double z, float partialTicks);

	public default void doRender(Tuple3d pos, float partialTicks)
	{
		this.doRender(pos.x, pos.y, pos.z, partialTicks);
	}
}
