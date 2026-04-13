package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import java.util.Deque;
import java.util.LinkedList;

@SideOnly(Side.CLIENT)
public abstract class TesselatorRenderable implements IRenderable {
	private VertexFormat format;
	private int glMode;

	@Nullable
	private CompiledVertexBuffer compiledBuffer;

	public TesselatorRenderable()
	{
		this.format = DefaultVertexFormats.POSITION_TEX;
		this.glMode = GL11.GL_QUADS;
		this.compiledBuffer = null;
	}

	public abstract void renderIntoBuffer(BufferBuilderWrapper bufferBuilder, float partialTicks);

	protected Tessellator getTesselator()
	{
		return Tessellator.getInstance();
	}

	protected void setVertexFormat(VertexFormat format)
	{
		this.format = format;
	}

	protected void setGlMode(int glMode)
	{
		this.glMode = glMode;
	}

	public int getGlMode()
	{
		return this.glMode;
	}

	public VertexFormat getVertexFormat()
	{
		return this.format;
	}

	public void invalidate()
	{
		this.compiledBuffer = null;
	}

	public void compile()
	{
		BufferBuilder builder = new BufferBuilder(1024*1024);
		builder.begin(this.glMode, this.format);
		this.renderIntoBuffer(new BufferBuilderWrapper(builder), 1F);
		this.compiledBuffer = CompiledVertexBuffer.compile(builder);
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		if(this.compiledBuffer != null)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			this.compiledBuffer.draw();
			GlStateManager.popMatrix();
		}
		else
		{
			Tessellator tessellator = this.getTesselator();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.setTranslation(x, y, z);
			bufferBuilder.begin(this.glMode, this.format);
			this.renderIntoBuffer(new BufferBuilderWrapper(bufferBuilder), partialTicks);
			tessellator.draw();
			bufferBuilder.setTranslation(0D, 0D, 0D);
		}
	}

	public static class BufferBuilderWrapper
	{
		private final BufferBuilder buffer;
		private final Deque<Point3d> translationStack;
		private final Point3d localTranslation;
		private final Point3d pushedSumTranslation;

		public BufferBuilderWrapper(BufferBuilder buffer)
		{
			this.localTranslation = new Point3d();
			this.pushedSumTranslation = new Point3d();
			this.translationStack = new LinkedList<>();
			this.buffer = buffer;
		}

		public void setTranslation(double x, double y, double z)
		{
			this.localTranslation.set(x, y, z);
		}

		public void setTranslation(Tuple3d pt)
		{
			this.setTranslation(pt.x, pt.y, pt.z);
		}

		public void pushTranslation()
		{
			Point3d pt = new Point3d(this.localTranslation);
			this.localTranslation.set(0, 0, 0);
			this.translationStack.add(pt);
			this.pushedSumTranslation.add(pt);
		}

		public void popTranslation()
		{
			Point3d pt = this.translationStack.pop();
			this.pushedSumTranslation.sub(pt);
			this.localTranslation.set(pt);
		}

		public BufferBuilderWrapper pos(double x, double y, double z)
		{
			this.buffer.pos(
					x + this.pushedSumTranslation.x + this.localTranslation.x,
					y + this.pushedSumTranslation.y + this.localTranslation.y,
					z + this.pushedSumTranslation.z + this.localTranslation.z
			);
			return this;
		}

		public BufferBuilderWrapper pos(Point3d point)
		{
			return this.pos(point.x, point.y, point.z);
		}

		public BufferBuilderWrapper tex(double u, double v)
		{
			this.buffer.tex(u, v);
			return this;
		}

		public BufferBuilderWrapper normal(float x, float y, float z)
		{
			this.buffer.normal(x, y, z);
			return this;
		}

		public void endVertex()
		{
			this.buffer.endVertex();
		}
	}
}
