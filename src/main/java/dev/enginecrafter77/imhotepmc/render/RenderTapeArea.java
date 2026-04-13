package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3d;
import dev.enginecrafter77.imhotepmc.util.math.Edge3d;
import net.minecraft.client.renderer.GlStateManager;

import javax.vecmath.Point3d;

public class RenderTapeArea extends TesselatorRenderable {
	private final RenderTapeFast tapeRender;
	private final Box3d box;

	private final Point3d boxCenter;
	private final Point3d edgeRenderPoint;

	public RenderTapeArea()
	{
		this.tapeRender = new RenderTapeFast();
		this.boxCenter = new Point3d();
		this.edgeRenderPoint = new Point3d();
		this.box = new Box3d();
	}

	public void setBox(Box3d box)
	{
		this.box.set(box);
	}

	public void setTapeThickness(double thickness)
	{
		this.tapeRender.setRadius(thickness);
	}

	public void setTapeSegmentLength(double segmentLength)
	{
		this.tapeRender.setSegmentLength(segmentLength);
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		RenderTapeFast.TAPE_SEGMENT_TEXTURE.bind();
		GlStateManager.disableCull();
		super.doRender(x, y, z, partialTicks);
		GlStateManager.enableCull();
	}

	@Override
	public void renderIntoBuffer(BufferBuilderWrapper bufferBuilder, float partialTicks)
	{
		VecUtil.boxCenter(this.box, this.boxCenter);

		bufferBuilder.pushTranslation();
		for(Edge3d edge : this.box.edges())
		{
			this.tapeRender.setEdge(edge);

			edge.pointAt(0.5D, this.edgeRenderPoint);
			this.edgeRenderPoint.sub(this.boxCenter);

			bufferBuilder.setTranslation(this.edgeRenderPoint);
			this.tapeRender.renderIntoBuffer(bufferBuilder, partialTicks);
		}
		bufferBuilder.popTranslation();
	}
}
