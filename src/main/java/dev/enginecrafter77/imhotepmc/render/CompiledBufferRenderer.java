package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.BufferBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CompiledBufferRenderer implements IAutoRenderable {
	private final BufferBuilder bufferBuilder;

	@Nullable
	private CompiledVertexBuffer buffer;

	public CompiledBufferRenderer(int bufferSize)
	{
		this.bufferBuilder = new BufferBuilder(bufferSize);
		this.buffer = null;
	}

	protected abstract void render(BufferBuilder builder);

	protected void invalidate()
	{
		this.buffer = null;
	}

	@Nonnull
	protected CompiledVertexBuffer getBuffer()
	{
		if(this.buffer == null)
		{
			this.bufferBuilder.reset();
			this.render(this.bufferBuilder);
			this.buffer = CompiledVertexBuffer.compile(this.bufferBuilder);
			this.bufferBuilder.finishDrawing();
		}
		return this.buffer;
	}

	@Override
	public void doRender(float partialTicks)
	{
		this.getBuffer().draw();
	}
}
