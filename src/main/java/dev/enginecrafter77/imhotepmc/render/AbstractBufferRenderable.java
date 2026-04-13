package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

/** @deprecated Use {@link TesselatorRenderable} */
@Deprecated
public abstract class AbstractBufferRenderable implements IBufferRenderable, IAutoRenderable {
	private VertexFormat format;
	private int glMode;

	public AbstractBufferRenderable()
	{
		this.format = DefaultVertexFormats.POSITION_TEX;
		this.glMode = GL11.GL_QUADS;
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

	@Override
	public void doRender(float partialTicks)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(this.glMode, this.format);
		this.render(bufferBuilder, partialTicks);
		tessellator.draw();
	}
}
