package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.entity.EntityConstructionTape;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderConstructionTape extends Render<EntityConstructionTape> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ImhotepMod.MOD_ID, "textures/entity/construction_tape.png");

	private final RenderTape tapeRender;

	public RenderConstructionTape(RenderManager renderManager)
	{
		super(renderManager);
		this.tapeRender = new RenderTape();
	}

	@Nonnull
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityConstructionTape entity)
	{
		return TEXTURE;
	}

	@Override
	public void doRender(@Nonnull EntityConstructionTape entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		this.tapeRender.setTexture(this.getEntityTexture(entity));
		this.tapeRender.setAnchors(entity.getFirstAnchor(), entity.getSecondAnchor());
		this.tapeRender.setRadius(entity.getRadius());
		this.tapeRender.setSegmentLength(1D);
		this.tapeRender.doRender(x, y, z, partialTicks);
	}
}
