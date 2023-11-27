package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.entity.EntityConstructionTape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

public class RenderConstructionTape extends Render<EntityConstructionTape> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ImhotepMod.MOD_ID, "textures/entity/construction_tape.png");
	private static final double BLOCKS_PER_TEX = 1D;

	private final Tessellator tessellator;
	private final Matrix4d transformMatrix;
	private final Point3d tpoint;

	public RenderConstructionTape(RenderManager renderManager)
	{
		super(renderManager);
		this.tessellator = new Tessellator(1024);
		this.transformMatrix = new Matrix4d();
		this.tpoint = new Point3d();
	}

	@Nonnull
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityConstructionTape entity)
	{
		return TEXTURE;
	}

	@Override
	public boolean shouldRender(EntityConstructionTape livingEntity, @Nonnull ICamera camera, double camX, double camY, double camZ)
	{
		return livingEntity.isInRangeToRender3d(camX, camY, camZ);
	}

	@Override
	public void doRender(@Nonnull EntityConstructionTape entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		switch(entity.getAxis())
		{
		case Y:
			this.transformMatrix.rotZ(Math.PI / 2D);
			break;
		case Z:
			this.transformMatrix.rotY(Math.PI / 2D);
			break;
		default:
			this.transformMatrix.setIdentity();
			break;
		}

		double r = entity.getRadius();
		double length = entity.getLength();
		int segments = (int)Math.round(length / BLOCKS_PER_TEX);
		double segmentLength = length / (segments * BLOCKS_PER_TEX);
		//int vertices = 8 * segments; // 2 quads (8 vertices) per segment

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getEntityTexture(entity));

		BufferBuilder builder = this.tessellator.getBuffer();
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		builder.setTranslation(x, y, z);

		double start = length / -2D;
		double end;
		for(int seg = 0; seg < segments; ++seg)
		{
			end = start + segmentLength;

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

		this.tessellator.draw();
	}

	private BufferBuilder putTransformedVertex(BufferBuilder builder, double x, double y, double z)
	{
		this.tpoint.set(x, y, z);
		this.transformMatrix.transform(this.tpoint);
		return builder.pos(this.tpoint.x, this.tpoint.y, this.tpoint.z);
	}
}
