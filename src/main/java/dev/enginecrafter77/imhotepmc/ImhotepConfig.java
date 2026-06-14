package dev.enginecrafter77.imhotepmc;

import net.minecraftforge.common.config.Config;

@Config(modid = ImhotepMod.MOD_ID)
public class ImhotepConfig {
	@Config.Name("cave_filler_max_blocks")
	@Config.Comment("The maximum number of cave blocks the cave filler is allowed to scan.")
	@Config.RangeInt(min = 1)
	@Config.LangKey("config.imhotepmc.general.cave_filler_max_blocks")
	public static int caveFillerMaxBlocks = 262144;
}
