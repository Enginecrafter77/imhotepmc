package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.util.ResourceLocation;

public class TextureSlice {
	private final ResourceLocation texture;
	private final int textureWidth;
	private final int textureHeight;

	private final int minU, minV, maxU, maxV;

	private final int width, height;

	private final float pminU, pminV, pmaxU, pmaxV;

	public TextureSlice(ResourceLocation texture, int textureWidth, int textureHeight, int minU, int minV, int maxU, int maxV)
	{
		this.texture = texture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.minU = minU;
		this.minV = minV;
		this.maxU = maxU;
		this.maxV = maxV;

		this.width = maxU - minU;
		this.height = maxV - minV;

		this.pminU = (float)minU / (float)this.textureWidth;
		this.pmaxU = (float)maxU / (float)this.textureWidth;
		this.pminV = (float)minV / (float)this.textureHeight;
		this.pmaxV = (float)maxV / (float)this.textureHeight;
	}

	public ResourceLocation getTexture()
	{
		return this.texture;
	}

	public int getTextureWidth()
	{
		return this.textureWidth;
	}

	public int getTextureHeight()
	{
		return this.textureHeight;
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}

	public int getMinU()
	{
		return this.minU;
	}

	public int getMinV()
	{
		return this.minV;
	}

	public int getMaxU()
	{
		return this.maxU;
	}

	public int getMaxV()
	{
		return this.maxV;
	}

	public float getPartialMinU()
	{
		return this.pminU;
	}

	public float getPartialMinV()
	{
		return this.pminV;
	}

	public float getPartialMaxU()
	{
		return this.pmaxU;
	}

	public float getPartialMaxV()
	{
		return this.pmaxV;
	}

	public TextureSlice sub(int x, int y, int w, int h)
	{
		int remW = this.getWidth() - x;
		int remH = this.getHeight() - y;
		if(w < 0 || w > remW || h < 0 || h > remH)
			throw new IndexOutOfBoundsException();
		return new TextureSlice(this.texture, this.textureWidth, this.textureHeight, this.minU + x, this.minV + y, this.minU + x + w, this.minV + y + h);
	}

	public static TextureSlice full(ResourceLocation texture, int width, int height)
	{
		return new TextureSlice(texture, width, height, 0, 0, width, height);
	}
}
