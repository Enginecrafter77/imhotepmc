package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class TexturePosition implements ReadableTexturePosition {
	public float minU;
	public float minV;
	public float maxU;
	public float maxV;

	public TexturePosition()
	{
		this.reset();
	}

	public void reset()
	{
		this.minU = 0F;
		this.minV = 0F;
		this.maxU = 1F;
		this.maxV = 1F;
	}

	public void set(float minU, float minV, float maxU, float maxV)
	{
		this.minU = minU;
		this.minV = minV;
		this.maxU = maxU;
		this.maxV = maxV;
	}

	public void set(TexturePosition other)
	{
		this.set(other.minU, other.minV, other.maxU, other.maxV);
	}

	public void set(TextureAtlasSprite sprite)
	{
		this.minU = sprite.getMinU();
		this.minV = sprite.getMinV();
		this.maxU = sprite.getMaxU();
		this.maxV = sprite.getMaxV();
	}

	@Override
	public float getMinU()
	{
		return this.minU;
	}

	@Override
	public float getMinV()
	{
		return this.minV;
	}

	@Override
	public float getMaxU()
	{
		return this.maxU;
	}

	@Override
	public float getMaxV()
	{
		return this.maxV;
	}
}
