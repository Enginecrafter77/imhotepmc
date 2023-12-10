package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

public class ItemRenderHelper implements IRenderable {
	private static final Vector3d RENDER_FACING_VECTOR = new Vector3d(0D, 0D, 1D);
	private static final Vector3d NULL_VECTOR = new Vector3d(0D, 0D, 0D);
	private static final double EPSILON = 0.001D;

	private final List<BakedQuad> quads;

	private final Vector3d rotationAxis;
	private final AxisAngle4d angle;

	private final Vector3d scale;

	@Nonnull
	private ItemStack stack;

	public ItemRenderHelper()
	{
		this.quads = new ArrayList<BakedQuad>(64);
		this.stack = ItemStack.EMPTY;

		this.rotationAxis = new Vector3d();
		this.angle = new AxisAngle4d();
		this.scale = new Vector3d();
	}

	public void setScale(double x, double y, double z)
	{
		this.scale.set(x, y, z);
	}

	public void setScale(double scale)
	{
		this.setScale(scale, scale, scale);
	}

	public void setRotation(double x, double y, double z, double angle)
	{
		this.angle.set(x, y, z, angle);
	}

	public void setRotationByVector(Vector3d facing)
	{
		this.rotationAxis.cross(RENDER_FACING_VECTOR, facing);
		if(this.rotationAxis.epsilonEquals(NULL_VECTOR, EPSILON))
			this.rotationAxis.set(0D, 1D, 0D);
		double angle = facing.angle(RENDER_FACING_VECTOR);
		this.angle.set(this.rotationAxis, angle);
	}

	public void setItem(ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate((float)Math.toDegrees(this.angle.angle), (float)this.angle.x, (float)this.angle.y, (float)this.angle.z);
		GlStateManager.scale(this.scale.x, this.scale.y, this.scale.z);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5D, 0.5D, -0.084375D);
		GlStateManager.rotate(180F, 0F, 0F, 1F);
		GlStateManager.scale(0.0625D, 0.0625D, 0.0009765625D);

		GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
		Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(this.stack, 0, 0);
		GlStateManager.cullFace(GlStateManager.CullFace.BACK);

		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}
}
