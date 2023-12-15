package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;

public class RecolorVertexTransformer implements IVertexConsumer {
	private final IVertexConsumer parent;

	// R G B A
	private final Matrix4f colorTransformer;
	private final Color4f transformedColor;

	public RecolorVertexTransformer(IVertexConsumer parent)
	{
		this.parent = parent;
		this.colorTransformer = new Matrix4f();
		this.transformedColor = new Color4f();
		this.colorTransformer.setIdentity();
	}

	@Override
	public VertexFormat getVertexFormat()
	{
		return this.parent.getVertexFormat();
	}

	@Override
	public void setQuadTint(int rgba)
	{
		float r = ((rgba >> 24) & 0xFF) / 255F;
		float g = ((rgba >> 16) & 0xFF) / 255F;
		float b = ((rgba >> 8) & 0xFF) / 255F;
		float a = (rgba & 0xFF) / 255F;
		this.setTint(r, g, b, a);
	}

	public void setTint(float r, float g, float b, float a)
	{
		this.colorTransformer.m00 = r;
		this.colorTransformer.m11 = g;
		this.colorTransformer.m22 = b;
		this.colorTransformer.m33 = a;
	}

	public void setTintMatrix(Matrix4f colorTransformer)
	{
		this.colorTransformer.set(colorTransformer);
	}

	@Override
	public void setQuadOrientation(EnumFacing orientation)
	{
		this.parent.setQuadOrientation(orientation);
	}

	@Override
	public void setApplyDiffuseLighting(boolean diffuse)
	{
		this.parent.setApplyDiffuseLighting(diffuse);
	}

	@Override
	public void setTexture(TextureAtlasSprite texture)
	{
		this.parent.setTexture(texture);
	}

	@Override
	public void put(int elementIndex, float... data)
	{
		VertexFormatElement element = this.getVertexFormat().getElement(elementIndex);
		if(element.getUsage() == VertexFormatElement.EnumUsage.COLOR)
		{
			this.transformedColor.set(data);
			this.colorTransformer.transform(this.transformedColor);
			this.transformedColor.get(data);
		}
		this.parent.put(elementIndex, data);
	}
}
