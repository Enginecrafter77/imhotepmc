package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.VertexTransformer;

import javax.vecmath.*;

public class RecolorVertexTransformer extends VertexTransformer {
	private final Matrix4f colorTransformer;
	private final Color4f transformedColor;

	public RecolorVertexTransformer(IVertexConsumer parent)
	{
		super(parent);
		this.colorTransformer = new Matrix4f();
		this.transformedColor = new Color4f();
		this.colorTransformer.setIdentity();
	}

	public void setTint(float r, float g, float b, float a)
	{
		this.colorTransformer.m00 = r;
		this.colorTransformer.m11 = g;
		this.colorTransformer.m22 = b;
		this.colorTransformer.m33 = a;
	}

	public void clearTint()
	{
		this.colorTransformer.setIdentity();
	}

	public void setTintMatrix(Matrix4f colorTransformer)
	{
		this.colorTransformer.set(colorTransformer);
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
		super.put(elementIndex, data);
	}
}
