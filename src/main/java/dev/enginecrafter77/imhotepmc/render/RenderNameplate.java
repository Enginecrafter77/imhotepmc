package dev.enginecrafter77.imhotepmc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;

public class RenderNameplate implements IRenderable {
	private final Vector3d renderPoint;

	@Nullable
	private FontRenderer fontRenderer;

	@Nullable
	private ITextComponent text;

	public RenderNameplate()
	{
		this.fontRenderer = null;
		this.renderPoint = new Vector3d();
		this.text = null;
	}

	public void setFontRenderer(FontRenderer fontRenderer)
	{
		this.fontRenderer = fontRenderer;
	}

	public void setText(@Nullable ITextComponent text)
	{
		this.text = text;
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		if(this.text == null)
			return;

		if(this.fontRenderer == null)
			this.fontRenderer = Minecraft.getMinecraft().fontRenderer;

		if(this.fontRenderer == null)
			return;

		this.renderPoint.set(x, y, z);
		if(this.renderPoint.length() > 64D)
			return;

		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewer == null)
			return;

		float yawInterpolation = viewer.prevRotationYaw + (viewer.rotationYaw - viewer.prevRotationYaw) * partialTicks;
		float pitchInterpolation = viewer.prevRotationPitch + (viewer.rotationPitch - viewer.prevRotationPitch) * partialTicks;
		EntityRenderer.drawNameplate(this.fontRenderer, this.text.getUnformattedText(), (float)x, (float)y, (float)z, 0, yawInterpolation, pitchInterpolation, false, false);
	}
}
