package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.Vector3d;
import net.minecraft.util.EnumFacing;

import java.util.Collection;
import java.util.EnumSet;

public class RenderCustomSizedCube extends TesselatorRenderable {
	private static final double V0 = -0.5D;
	private static final double V1 = 0.5D;

	private final Vector3d size;
	private final TexturePosition[] texturePositions;
	private final TexturePosition activePosition;
	private final EnumSet<EnumFacing> renderedSides;

	public RenderCustomSizedCube()
	{
		this.size = new Vector3d();
		this.texturePositions = new TexturePosition[6];
		this.activePosition = new TexturePosition();
		this.renderedSides = EnumSet.allOf(EnumFacing.class);

		this.setSize(1D, 1D, 1D);
		for(int i = 0; i < 6; ++i)
			this.texturePositions[i] = new TexturePosition();
	}

	public void setSize(double x, double y, double z)
	{
		this.size.x = x;
		this.size.y = y;
		this.size.z = z;
	}

	public void setRenderedSides(Collection<EnumFacing> sides)
	{
		this.renderedSides.clear();
		this.renderedSides.addAll(sides);
	}

	public void setTextureUV(EnumFacing facing, ReadableTexturePosition position)
	{
		this.texturePositions[facing.ordinal()].set(position);
	}

	public void setAllTextureUVs(ReadableTexturePosition position)
	{
		this.setTextureUV(EnumFacing.UP, position);
		this.setTextureUV(EnumFacing.NORTH, position);
		this.setTextureUV(EnumFacing.SOUTH, position);
		this.setTextureUV(EnumFacing.EAST, position);
		this.setTextureUV(EnumFacing.WEST, position);
		this.setTextureUV(EnumFacing.DOWN, position);
	}
	
	private void activate(EnumFacing face)
	{
		this.activePosition.set(this.texturePositions[face.ordinal()]);
	}

	private BufferBuilderWrapper tPos(BufferBuilderWrapper bufferBuilder, double x, double y, double z)
	{
		return bufferBuilder.pos(
				x * this.size.x,
				y * this.size.y,
				z * this.size.z);
	}

	private void renderSide(BufferBuilderWrapper builder, EnumFacing side)
	{
		this.activate(side);
		switch(side)
		{
		case DOWN:
			this.tPos(builder, V1, V0, V1).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
			this.tPos(builder, V0, V0, V1).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V0, V0, V0).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V1, V0, V0).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();
			break;
		case UP:
			this.tPos(builder, V1, V1, V0).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
			this.tPos(builder, V0, V1, V0).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V0, V1, V1).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V1, V1, V1).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();
			break;
		case NORTH:
			this.tPos(builder, V1, V1, V0).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
			this.tPos(builder, V1, V0, V0).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V0, V0, V0).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V0, V1, V0).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();
			break;
		case SOUTH:
			this.tPos(builder, V0, V1, V1).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
			this.tPos(builder, V0, V0, V1).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V1, V0, V1).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V1, V1, V1).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();
			break;
		case WEST:
			this.tPos(builder, V0, V1, V0).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
			this.tPos(builder, V0, V0, V0).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V0, V0, V1).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V0, V1, V1).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();
			break;
		case EAST:
			this.tPos(builder, V1, V1, V1).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
			this.tPos(builder, V1, V0, V1).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V1, V0, V0).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
			this.tPos(builder, V1, V1, V0).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();
			break;
		}
	}

	@Override
	public void renderIntoBuffer(BufferBuilderWrapper builder, float partialTicks)
	{
		for(EnumFacing side : this.renderedSides)
			this.renderSide(builder, side);
	}
}
