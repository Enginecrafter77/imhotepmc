package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.vecmath.*;

public class RenderTape implements IRenderable {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ImhotepMod.MOD_ID, "textures/entity/construction_tape.png");
	public static final double HALF_PI = Math.PI / 2D;

	private final Tessellator tessellator;
	private final Matrix4d transformMatrix;
	private final Point3d tpoint;

	private final Point3d a1, a2;
	private final Vector3d heading;
	private final AxisAngle4d rot;

	private ResourceLocation texture;
	private double segmentLength;
	private double radius;

	public RenderTape()
	{
		this.tessellator = new Tessellator(1024);
		this.transformMatrix = new Matrix4d();
		this.a1 = new Point3d();
		this.a2 = new Point3d();
		this.heading = new Vector3d();
		this.rot = new AxisAngle4d();
		this.tpoint = new Point3d();
		this.texture = null;
		this.radius = 0.0625D; // 1/16th of block
		this.segmentLength = 1D;
	}

	public void setTexture(ResourceLocation texture)
	{
		this.texture = texture;
	}

	public void setAnchors(Tuple3d a1, Tuple3d a2)
	{
		this.a1.set(a1);
		this.a2.set(a2);
	}

	public void setAnchors(Vec3d v1, Vec3d v2)
	{
		VecUtil.copyVec3d(v1, this.a1);
		VecUtil.copyVec3d(v2, this.a2);
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	public void setSegmentLength(double segmentLength)
	{
		this.segmentLength = segmentLength;
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		this.heading.set(this.a2);
		this.heading.sub(this.a1);
		double length = this.heading.length();

		this.heading.normalize();
		this.rot.set(this.heading.x, this.heading.z, this.heading.y, HALF_PI);
		this.transformMatrix.setIdentity();
		this.transformMatrix.set(this.rot);

		double r = this.radius;
		int segments = (int)Math.round(length / this.segmentLength);
		double stretchedSegmentLength = length / (segments * this.segmentLength);

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);

		BufferBuilder builder = this.tessellator.getBuffer();
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		builder.setTranslation(x, y, z);

		double start = length / -2D;
		double end;
		for(int seg = 0; seg < segments; ++seg)
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

		GlStateManager.disableCull();
		this.tessellator.draw();
		GlStateManager.enableCull();
	}

	private BufferBuilder putTransformedVertex(BufferBuilder builder, double x, double y, double z)
	{
		this.tpoint.set(x, y, z);
		this.transformMatrix.transform(this.tpoint);
		return builder.pos(this.tpoint.x, this.tpoint.y, this.tpoint.z);
	}
}
