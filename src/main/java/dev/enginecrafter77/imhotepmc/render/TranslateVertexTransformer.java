package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.VertexTransformer;

import javax.vecmath.*;

public class TranslateVertexTransformer extends VertexTransformer {
	private final Matrix4d positionTransformer;
	private final Vector3d positionTranslate;
	private final Point3f transformedPosition;

	public TranslateVertexTransformer(IVertexConsumer parent)
	{
		super(parent);
		this.positionTransformer = new Matrix4d();
		this.positionTranslate = new Vector3d();
		this.transformedPosition = new Point3f();
		this.positionTransformer.setIdentity();
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
		if(element.getUsage() == VertexFormatElement.EnumUsage.POSITION)
		{
			this.transformedPosition.set(data);
			this.positionTransformer.transform(this.transformedPosition);
			this.transformedPosition.get(data);
		}
		super.put(elementIndex, data);
	}
}
