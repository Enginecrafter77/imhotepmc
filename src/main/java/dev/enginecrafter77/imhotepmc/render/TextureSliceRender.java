package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.*;

public class TextureSliceRender implements IRenderable {
	private static final Vector3d RENDER_FACING_VECTOR = new Vector3d(0D, 0D, 1D);
	private static final double EPSILON = 0.0001D;

	private final Matrix4d rotationMatrix;
	private final Matrix4d translateMatrix;
	private final Matrix4d scaleMatrix;

	private final AxisAngle4d angle;
	private final Vector3d axis;

	private final Vector3d translation;
	private final Point3d drawPoint;

	@Nullable
	private TextureSlice texture;

	public TextureSliceRender()
	{
		this.translation = new Vector3d();
		this.drawPoint = new Point3d();
		this.rotationMatrix = new Matrix4d();
		this.translateMatrix = new Matrix4d();
		this.scaleMatrix = new Matrix4d();
		this.angle = new AxisAngle4d();
		this.axis = new Vector3d();
		this.texture = null;

		this.rotationMatrix.setIdentity();
		this.translateMatrix.setIdentity();
		this.scaleMatrix.setIdentity();
	}

	public void setRotation(AxisAngle4d angle)
	{
		this.axis.set(angle.x, angle.y, angle.z);
		this.angle.set(angle);
		this.rotationMatrix.setRotation(angle);
	}

	public void setRotation(Vector3d axis, double angle)
	{
		this.setRotation(axis.x, axis.y, axis.z, angle);
	}

	public void setRotationVector(Vector3d vec)
	{
		this.axis.cross(RENDER_FACING_VECTOR, vec);

		if(this.axis.length() < EPSILON) // The vectors are parallel
			this.axis.set(0D, 1D, 0D);

		double angle = RENDER_FACING_VECTOR.angle(vec);
		this.setRotation(this.axis, angle);
	}

	public void setRotation(double x, double y, double z, double angle)
	{
		this.axis.set(x, y, z);
		this.angle.set(x, y, z, angle);
		this.rotationMatrix.setIdentity();
		this.rotationMatrix.setRotation(this.angle);
	}

	public void setSize(Tuple2d size)
	{
		this.setSize(size.x, size.y);
	}

	public void setSize(double x, double y)
	{
		this.scaleMatrix.setIdentity();
		this.scaleMatrix.m00 = x;
		this.scaleMatrix.m11 = y;
	}
	
	public void setTexture(@Nullable TextureSlice texture)
	{
		this.texture = texture;
	}

	@Nullable
	public TextureSlice getTexture()
	{
		return this.texture;
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		if(this.texture == null)
			return;
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture.getTexture());

		float minU = this.texture.getPartialMinU();
		float minV = this.texture.getPartialMinV();
		float maxU = this.texture.getPartialMaxU();
		float maxV = this.texture.getPartialMaxV();
		
		this.translation.set(x, y, z);
		this.translateMatrix.setIdentity();
		this.translateMatrix.setTranslation(this.translation);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		this.putTransformedPos(builder, -0.5D, 0.5D).tex(minU, minV).endVertex(); // top left
		this.putTransformedPos(builder, -0.5D, -0.5D).tex(minU, maxV).endVertex(); // bottom left
		this.putTransformedPos(builder, 0.5D, -0.5D).tex(maxU, maxV).endVertex(); // bottom right
		this.putTransformedPos(builder, 0.5D, 0.5D).tex(maxU, minV).endVertex(); // top right
		tessellator.draw();
	}

	private BufferBuilder putTransformedPos(BufferBuilder builder, double x, double y)
	{
		this.drawPoint.set(x, y, 0D);
		this.scaleMatrix.transform(this.drawPoint);
		this.rotationMatrix.transform(this.drawPoint);
		this.translateMatrix.transform(this.drawPoint);
		return builder.pos(this.drawPoint.x, this.drawPoint.y, this.drawPoint.z);
	}
}
