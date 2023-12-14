package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import java.nio.ByteBuffer;
import java.util.List;

public class CompiledVertexBuffer {
	private final ByteBuffer bytebuffer;
	private final VertexFormat vertexformat;
	private final int vertexCount;
	private final int glDrawMode;

	public CompiledVertexBuffer(ByteBuffer buffer, int vertexCount, int glDrawMode, VertexFormat vertexFormat)
	{
		this.bytebuffer = buffer;
		this.vertexCount = vertexCount;
		this.glDrawMode = glDrawMode;
		this.vertexformat = vertexFormat;
	}

	public void draw()
	{
		if(this.vertexCount == 0)
			return;

		int vertexStride = this.vertexformat.getSize();
		List<VertexFormatElement> list = this.vertexformat.getElements();

		for(int elementIndex = 0; elementIndex < list.size(); ++elementIndex)
		{
			VertexFormatElement vertexformatelement = list.get(elementIndex);
			this.bytebuffer.position(this.vertexformat.getOffset(elementIndex));
			vertexformatelement.getUsage().preDraw(this.vertexformat, elementIndex, vertexStride, this.bytebuffer);
		}

		GlStateManager.glDrawArrays(this.glDrawMode, 0, this.vertexCount);

		for(int elementIndex = 0; elementIndex < list.size(); ++elementIndex)
		{
			VertexFormatElement element = list.get(elementIndex);
			element.getUsage().postDraw(this.vertexformat, elementIndex, vertexStride, this.bytebuffer);
		}
	}

	public static CompiledVertexBuffer compile(BufferBuilder builder)
	{
		ByteBuffer source = builder.getByteBuffer();

		ByteBuffer copy = ByteBuffer.allocateDirect(source.limit());
		copy.put(source);
		copy.flip();
		return new CompiledVertexBuffer(source, builder.getVertexCount(), builder.getDrawMode(), builder.getVertexFormat());
	}
}
