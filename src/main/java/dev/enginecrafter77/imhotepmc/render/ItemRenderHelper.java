package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import org.lwjgl.opengl.GL11;

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

	protected List<BakedQuad> collectQuads()
	{
		WorldClient worldClient = Minecraft.getMinecraft().world;
		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(this.stack, worldClient, null);

		this.quads.clear();
		for(EnumFacing face : EnumFacing.values())
			this.quads.addAll(model.getQuads(null, face, worldClient.getSeed()));
		this.quads.addAll(model.getQuads(null, null, worldClient.getSeed()));
		return this.quads;
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

		this.collectQuads();
		this.pipeQuads(builder);

		GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.pushMatrix();
				GlStateManager.rotate((float)Math.toDegrees(this.angle.angle), (float)this.angle.x, (float)this.angle.y, (float)this.angle.z);
				GlStateManager.scale(this.scale.x, this.scale.y, this.scale.z);
				GlStateManager.pushMatrix();
					GlStateManager.translate(-0.5D, -0.5D, -0.5D);
					Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					tessellator.draw();
				GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	private void pipeQuads(BufferBuilder builder)
	{
		VertexBufferConsumer consumer = new VertexBufferConsumer(builder);
		for(BakedQuad quad : this.quads)
			quad.pipe(consumer);
	}
}
