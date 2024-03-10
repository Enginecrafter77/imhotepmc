package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.entity.EntityPrimedRestorationCharge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.vecmath.Vector3f;

public class RenderEntityPrimedRestorationCharge extends RenderEntity {
	private final Vector3f tint;
	private final RenderCube renderCube;

	private final TextureAtlasSprite texUp;
	private final TextureAtlasSprite texOther;

	public RenderEntityPrimedRestorationCharge(RenderManager renderManagerIn)
	{
		super(renderManagerIn);
		this.tint = new Vector3f();
		this.renderCube = new RenderCube(this::getTextureOn);

		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		this.texUp = map.getAtlasSprite(ImhotepMod.MOD_ID + ":blocks/restoration_charge_top");
		this.texOther = map.getAtlasSprite(ImhotepMod.MOD_ID + ":blocks/restoration_charge");
	}

	protected void getTextureOn(EnumFacing face, TexturePosition texturePosition)
	{
		switch(face)
		{
		case UP:
		case DOWN:
			texturePosition.set(this.texUp);
			break;
		default:
			texturePosition.set(this.texOther);
			break;
		}
	}

	@Nonnull
	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getEntityTexture(entity));
		EntityPrimedRestorationCharge chargeEntity = (EntityPrimedRestorationCharge)entity;

		if(chargeEntity.getFuse() % 10 > 5)
			this.tint.set(0.5F, 0.5F, 0.5F);
		else
			this.tint.set(1F, 1F, 1F);

		double inflation = 1D;
		if(chargeEntity.getFuse() < 8)
			inflation = 1D + 0.25D * Math.pow(1D - (double)chargeEntity.getFuse() / 8D, 2D);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.5D * entity.height * inflation, z);
		GlStateManager.rotate(entityYaw, 0F, 1F, 0F);
		GlStateManager.pushMatrix();
		GlStateManager.scale(entity.width * inflation, entity.height * inflation, entity.width * inflation);
		GlStateManager.color(this.tint.x, this.tint.y, this.tint.z);
		this.renderCube.doRender(partialTicks);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}
}
