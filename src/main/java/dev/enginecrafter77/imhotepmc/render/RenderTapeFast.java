package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.util.math.Edge3d;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class RenderTapeFast extends TesselatorRenderable {
	public static final TextureSlice TAPE_SEGMENT_TEXTURE = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/entity/construction_tape.png"), 64, 16);

	private static final Vector3d DRAW_VECTOR = new Vector3d(1D, 0D, 0D);

	// settings
	private final Edge3d edge;
	private double segmentLength;
	private double radius;

	// intermediate (potentially precomputed) results
	private final Matrix4d transformMatrix;
	private final Point3d tpoint;
	private final Vector3d heading;
	private final Vector3d drawCrossProduct;
	private final AxisAngle4d rot;
	private int segmentCount;
	private double length;
	private double stretchedSegmentLength;

	public RenderTapeFast()
	{
		this.transformMatrix = new Matrix4d();
		this.edge = new Edge3d();
		this.heading = new Vector3d();
		this.rot = new AxisAngle4d();
		this.tpoint = new Point3d();
		this.drawCrossProduct = new Vector3d();
		this.radius = 0.0625D; // 1/16th of block
		this.segmentLength = 1D;
	}

	public void setEdge(Edge3d edge)
	{
		this.edge.set(edge);
		this.updateParameters();
	}

	public void setEdge(Point3d p1, Point3d p2)
	{
		this.edge.set(p1, p2);
		this.updateParameters();
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
		this.updateParameters();
	}

	public void setSegmentLength(double segmentLength)
	{
		this.segmentLength = segmentLength;
		this.updateParameters();
	}

	private void updateParameters()
	{
		this.length = this.edge.length();
		if(this.length < 0.01D)
			return;
		this.edge.deltas(this.heading);
		this.drawCrossProduct.cross(DRAW_VECTOR, this.heading);
		double angle = DRAW_VECTOR.angle(this.heading);
		this.rot.set(this.drawCrossProduct, angle);
		this.transformMatrix.setIdentity();
		this.transformMatrix.set(this.rot);
		this.segmentCount = (int)Math.round(this.length / this.segmentLength);
		this.stretchedSegmentLength = this.length / (this.segmentCount * this.segmentLength);
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		TAPE_SEGMENT_TEXTURE.bind();
		GlStateManager.disableCull();
		super.doRender(x, y, z, partialTicks);
		GlStateManager.enableCull();
	}

	@Override
	public void renderIntoBuffer(BufferBuilderWrapper builder, float partialTicks)
	{
		if(this.length < 0.01D)
			return;

		double start = this.length * -0.5D;
		double end;

		double r = this.radius;
		for(int seg = 0; seg < this.segmentCount; ++seg)
		{
			end = start + stretchedSegmentLength;

			this.putTransformedVertex(builder, start, r, r).tex(0F, 1F).endVertex();
			this.putTransformedVertex(builder, end, r, r).tex(1F, 0F).endVertex();
			this.putTransformedVertex(builder, end, -r, -r).tex(1F, 1F).endVertex();
			this.putTransformedVertex(builder, start, -r, -r).tex(0F, 1F).endVertex();

			this.putTransformedVertex(builder, start, -r, r).tex(0F, 0F).endVertex();
			this.putTransformedVertex(builder, end, -r, r).tex(1F, 0F).endVertex();
			this.putTransformedVertex(builder, end, r, -r).tex(1F, 1F).endVertex();
			this.putTransformedVertex(builder, start, r, -r).tex(0F, 1F).endVertex();

			start = end;
		}
	}

	private BufferBuilderWrapper putTransformedVertex(BufferBuilderWrapper builder, double x, double y, double z)
	{
		this.tpoint.set(x, y, z);
		this.transformMatrix.transform(this.tpoint);
		return builder.pos(this.tpoint.x, this.tpoint.y, this.tpoint.z);
	}
}
