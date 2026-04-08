package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderFluidPumpPipe implements IRenderable {
	private static final TextureSlice PIPE_TEX = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/misc/pump_pipe.png"), 32, 64);
	private static final TextureSlice PIPE_TOP = PIPE_TEX.sub(16, 0, 16, 16);
	private static final TextureSlice PIPE_MID = PIPE_TEX.sub(0, 0, 16, 48);
	private static final TextureSlice PIPE_GATE = PIPE_TEX.sub(0, 48, 16, 16);
	private static final TextureSlice PIPE_BOTTOM = PIPE_TEX.sub(16, 48, 16, 16);

	private final RenderCustomSizedCube midsectionRenderer;
	private final RenderCustomSizedCube endSegmentRenderer;
	private final RenderCustomSizedCube capRenderer;

	private double thickness;
	private double segmentLength;
	private double capSize;
	private double bottomClearance;
	private double endSegmentLength; // calculated
	private int segments;

	public RenderFluidPumpPipe()
	{
		this.thickness = 0.375D;
		this.segmentLength = 0.75D;
		this.capSize = 0.2D;
		this.bottomClearance = 0.1D;
		this.segments = 1;

		this.midsectionRenderer = new RenderCustomSizedCube();
		this.capRenderer = new RenderCustomSizedCube();
		this.endSegmentRenderer = new RenderCustomSizedCube();

		this.midsectionRenderer.setAllTextureUVs(PIPE_MID.asPosition());
		this.midsectionRenderer.setTextureUV(EnumFacing.UP, PIPE_TOP.asPosition());
		this.midsectionRenderer.setTextureUV(EnumFacing.DOWN, PIPE_TOP.asPosition());

		this.capRenderer.setAllTextureUVs(PIPE_GATE.asPosition());
		this.capRenderer.setTextureUV(EnumFacing.UP, PIPE_TOP.asPosition());
		this.capRenderer.setTextureUV(EnumFacing.DOWN, PIPE_BOTTOM.asPosition());

		this.updateModelSizes();
	}

	public void setSegmentCount(int segments)
	{
		this.segments = segments;
	}

	public void setSegmentLength(double segmentLength)
	{
		this.segmentLength = segmentLength;
		this.updateModelSizes();
	}

	public void setCapSize(double capSize)
	{
		this.capSize = capSize;
		this.updateModelSizes();
	}

	public void setThickness(double thickness)
	{
		this.thickness = thickness;
		this.updateModelSizes();
	}

	public void setBottomClearance(double bottomClearance)
	{
		this.bottomClearance = bottomClearance;
		this.updateModelSizes();
	}

	private void updateModelSizes()
	{
		this.endSegmentLength = this.segmentLength - this.capSize - this.bottomClearance;
		this.midsectionRenderer.setSize(this.thickness, this.segmentLength, this.thickness);
		this.capRenderer.setSize(this.thickness, this.capSize, this.thickness);
		this.endSegmentRenderer.setSize(this.thickness, this.endSegmentLength, this.thickness);

		TexturePosition midCut = new TexturePosition();
		midCut.set(PIPE_MID.asPosition());
		midCut.maxV = (float)(midCut.minV + (midCut.maxV - midCut.minV) * (this.endSegmentLength / this.segmentLength));
		this.endSegmentRenderer.setAllTextureUVs(midCut);
		this.endSegmentRenderer.setTextureUV(EnumFacing.UP, PIPE_TOP.asPosition());
		this.endSegmentRenderer.setTextureUV(EnumFacing.DOWN, PIPE_TOP.asPosition());
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		if(this.segments == 0)
			return;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		PIPE_TEX.bind();
		for(int i = 1; i < this.segments; ++i) // start at 1 to avoid rendering end segment
		{
			this.midsectionRenderer.setPosition(x, y - (this.segmentLength * 0.5D), z);
			this.midsectionRenderer.render(builder, partialTicks);
			y -= this.segmentLength;
		}

		this.endSegmentRenderer.setPosition(x, y - (this.endSegmentLength * 0.5D), z);
		this.endSegmentRenderer.render(builder, partialTicks);
		y -= this.endSegmentLength;

		this.capRenderer.setPosition(x, y - (this.capSize * 0.5D), z);
		this.capRenderer.render(builder, partialTicks);

		tessellator.draw();
	}
}
