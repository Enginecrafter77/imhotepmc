package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.util.EnumFacing;

public class RenderCustomSizedCube implements IBufferRenderable {
	private static final double V0 = -0.5D;
	private static final double V1 = 0.5D;

	private final Vector3d size;
	private final Vector3d offset;
	private final TexturePosition[] texturePositions;
	private final TexturePosition activePosition;

	public RenderCustomSizedCube()
	{
		this.size = new Vector3d();
		this.texturePositions = new TexturePosition[6];
		this.activePosition = new TexturePosition();
		this.offset = new Vector3d();

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

	public void setPosition(double x, double y, double z)
	{
		this.offset.x = x;
		this.offset.y = y;
		this.offset.z = z;
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

	private BufferBuilder tPos(BufferBuilder bufferBuilder, double x, double y, double z)
	{
		return bufferBuilder.pos(
				x * this.size.x + this.offset.x,
				y * this.size.y + this.offset.y,
				z * this.size.z + this.offset.z
		);
	}

	@Override
	public void render(BufferBuilder builder, float partialTicks)
	{
		this.activate(EnumFacing.DOWN);
		this.tPos(builder, V1, V0, V1).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
		this.tPos(builder, V0, V0, V1).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V0, V0, V0).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V1, V0, V0).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();

		this.activate(EnumFacing.NORTH);
		this.tPos(builder, V0, V1, V0).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
		this.tPos(builder, V0, V0, V0).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V0, V0, V1).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V0, V1, V1).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();

		this.activate(EnumFacing.WEST);
		this.tPos(builder, V1, V1, V0).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
		this.tPos(builder, V1, V0, V0).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V0, V0, V0).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V0, V1, V0).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();

		this.activate(EnumFacing.EAST);
		this.tPos(builder, V0, V1, V1).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
		this.tPos(builder, V0, V0, V1).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V1, V0, V1).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V1, V1, V1).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();

		this.activate(EnumFacing.SOUTH);
		this.tPos(builder, V1, V1, V1).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
		this.tPos(builder, V1, V0, V1).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V1, V0, V0).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V1, V1, V0).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();

		this.activate(EnumFacing.UP);
		this.tPos(builder, V1, V1, V0).tex(this.activePosition.minU, this.activePosition.minV).endVertex();
		this.tPos(builder, V0, V1, V0).tex(this.activePosition.minU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V0, V1, V1).tex(this.activePosition.maxU, this.activePosition.maxV).endVertex();
		this.tPos(builder, V1, V1, V1).tex(this.activePosition.maxU, this.activePosition.minV).endVertex();
	}
}
