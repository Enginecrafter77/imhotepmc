package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class RenderCube extends CompiledBufferRenderer {
	private static final double V0 = -0.5D;
	private static final double V1 = 0.5D;

	private final TexturePosition texturePosition;
	private final TextureSideMapper textureSideMapper;

	public RenderCube(TextureSideMapper textureSideMapper)
	{
		super(64);
		this.texturePosition = new TexturePosition();
		this.textureSideMapper = textureSideMapper;
	}

	@Override
	protected void render(BufferBuilder builder)
	{
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		this.textureSideMapper.getTexturePos(EnumFacing.DOWN, this.texturePosition);
		builder.pos(V1, V0, V1).tex(this.texturePosition.minU, this.texturePosition.minV).endVertex();
		builder.pos(V0, V0, V1).tex(this.texturePosition.minU, this.texturePosition.maxV).endVertex();
		builder.pos(V0, V0, V0).tex(this.texturePosition.maxU, this.texturePosition.maxV).endVertex();
		builder.pos(V1, V0, V0).tex(this.texturePosition.maxU, this.texturePosition.minV).endVertex();

		this.textureSideMapper.getTexturePos(EnumFacing.NORTH, this.texturePosition);
		builder.pos(V0, V1, V0).tex(this.texturePosition.minU, this.texturePosition.minV).endVertex();
		builder.pos(V0, V0, V0).tex(this.texturePosition.minU, this.texturePosition.maxV).endVertex();
		builder.pos(V0, V0, V1).tex(this.texturePosition.maxU, this.texturePosition.maxV).endVertex();
		builder.pos(V0, V1, V1).tex(this.texturePosition.maxU, this.texturePosition.minV).endVertex();

		this.textureSideMapper.getTexturePos(EnumFacing.WEST, this.texturePosition);
		builder.pos(V1, V1, V0).tex(this.texturePosition.minU, this.texturePosition.minV).endVertex();
		builder.pos(V1, V0, V0).tex(this.texturePosition.minU, this.texturePosition.maxV).endVertex();
		builder.pos(V0, V0, V0).tex(this.texturePosition.maxU, this.texturePosition.maxV).endVertex();
		builder.pos(V0, V1, V0).tex(this.texturePosition.maxU, this.texturePosition.minV).endVertex();

		this.textureSideMapper.getTexturePos(EnumFacing.EAST, this.texturePosition);
		builder.pos(V0, V1, V1).tex(this.texturePosition.minU, this.texturePosition.minV).endVertex();
		builder.pos(V0, V0, V1).tex(this.texturePosition.minU, this.texturePosition.maxV).endVertex();
		builder.pos(V1, V0, V1).tex(this.texturePosition.maxU, this.texturePosition.maxV).endVertex();
		builder.pos(V1, V1, V1).tex(this.texturePosition.maxU, this.texturePosition.minV).endVertex();

		this.textureSideMapper.getTexturePos(EnumFacing.SOUTH, this.texturePosition);
		builder.pos(V1, V1, V1).tex(this.texturePosition.minU, this.texturePosition.minV).endVertex();
		builder.pos(V1, V0, V1).tex(this.texturePosition.minU, this.texturePosition.maxV).endVertex();
		builder.pos(V1, V0, V0).tex(this.texturePosition.maxU, this.texturePosition.maxV).endVertex();
		builder.pos(V1, V1, V0).tex(this.texturePosition.maxU, this.texturePosition.minV).endVertex();

		this.textureSideMapper.getTexturePos(EnumFacing.UP, this.texturePosition);
		builder.pos(V1, V1, V0).tex(this.texturePosition.minU, this.texturePosition.minV).endVertex();
		builder.pos(V0, V1, V0).tex(this.texturePosition.minU, this.texturePosition.maxV).endVertex();
		builder.pos(V0, V1, V1).tex(this.texturePosition.maxU, this.texturePosition.maxV).endVertex();
		builder.pos(V1, V1, V1).tex(this.texturePosition.maxU, this.texturePosition.minV).endVertex();
	}

	public interface TextureSideMapper {
		public void getTexturePos(EnumFacing facing, TexturePosition tex);
	}
}
