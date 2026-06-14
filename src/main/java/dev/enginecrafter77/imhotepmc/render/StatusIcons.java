package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.util.ResourceLocation;

public class StatusIcons {
	private static final TextureSlice ICON_PACK = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/misc/status_icons.png"), 32, 32);
	public static final TextureSlice NO_POWER = ICON_PACK.sub(0, 0, 16, 16);
	public static final TextureSlice NO_BLOCKS = ICON_PACK.sub(16, 0, 16, 16);
	public static final TextureSlice PAUSED = ICON_PACK.sub(0, 16, 16, 16);
	public static final TextureSlice DONE = ICON_PACK.sub(16, 16, 16, 16);
}
