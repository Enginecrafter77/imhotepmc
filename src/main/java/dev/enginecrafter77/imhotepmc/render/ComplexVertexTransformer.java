package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.VertexTransformer;

import javax.vecmath.*;

public class ComplexVertexTransformer extends VertexTransformer {
	private final Matrix4d positionTransformer;
	private final Vector3d positionTranslate;
	private final Point3f transformedPosition;

	private final Matrix4f colorTransformer;
	private final Color4f transformedColor;

	public ComplexVertexTransformer(IVertexConsumer parent)
	{
		super(parent);
		this.positionTransformer = new Matrix4d();
		this.positionTranslate = new Vector3d();
		this.transformedPosition = new Point3f();
		this.colorTransformer = new Matrix4f();
		this.transformedColor = new Color4f();

		this.positionTransformer.setIdentity();
		this.colorTransformer.setIdentity();
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

	public void setOffset(Tuple3d offset)
	{
		this.setOffset(offset.x, offset.y, offset.z);
	}

	public void setOffset(Vec3d offset)
	{
		this.setOffset(offset.x, offset.y, offset.z);
	}

	public void setOffset(Vec3i offset)
	{
		this.setOffset(offset.getX(), offset.getY(), offset.getZ());
	}

	public void setOffset(double x, double y, double z)
	{
		this.positionTranslate.set(x, y, z);
		this.positionTransformer.setIdentity();
		this.positionTransformer.setTranslation(this.positionTranslate);
	}

	public void setPositionTransform(Matrix4f positionTransform)
	{
		this.positionTransformer.set(positionTransform);
	}

	@Override
	public void put(int elementIndex, float... data)
	{
		VertexFormatElement element = this.getVertexFormat().getElement(elementIndex);
		switch(element.getUsage())
		{
		case POSITION:
			this.transformedPosition.set(data);
			this.positionTransformer.transform(this.transformedPosition);
			this.transformedPosition.get(data);
			break;
		case COLOR:
			this.transformedColor.set(data);
			this.colorTransformer.transform(this.transformedColor);
			this.transformedColor.get(data);
			break;
		}
		super.put(elementIndex, data);
	}
}
