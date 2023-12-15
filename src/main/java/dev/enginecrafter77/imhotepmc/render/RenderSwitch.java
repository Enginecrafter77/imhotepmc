package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.util.ResourceLocation;

public class RenderSwitch extends TextureSliceRender {
	private static final TextureSlice TEX_SWITCH = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/misc/switch.png"), 4, 16);
	private static final TextureSlice TEX_SWITCH_OFF = TEX_SWITCH.sub(0, 0, 4, 8);
	private static final TextureSlice TEX_SWITCH_ON = TEX_SWITCH.sub(0, 8, 4, 8);

	private boolean active;

	public RenderSwitch()
	{
		this.setActive(false);
	}

	public void setActive(boolean active)
	{
		this.active = active;
		this.setTexture(active ? TEX_SWITCH_ON : TEX_SWITCH_OFF);
	}

	public boolean isActive()
	{
		return this.active;
	}
}
