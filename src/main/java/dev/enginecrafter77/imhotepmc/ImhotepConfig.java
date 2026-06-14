package dev.enginecrafter77.imhotepmc;

import net.minecraftforge.common.config.Config;

@Config(modid = ImhotepMod.MOD_ID, category = "")
public class ImhotepConfig {
	@Config.Name("general")
	@Config.LangKey("config.imhotepmc.general")
	public static final General general = new General();
	@Config.Name("energy")
	@Config.LangKey("config.imhotepmc.energy")
	public static final EnergyCosts energy = new EnergyCosts();

	public static class General {
		@Config.Name("cave_filler_max_blocks")
		@Config.Comment("The maximum number of cave blocks the cave filler is allowed to scan.")
		@Config.RangeInt(min = 1)
		@Config.LangKey("config.imhotepmc.general.cave_filler_max_blocks")
		public int caveFillerMaxBlocks = 262144;
	}

	public static class EnergyCosts {
		@Config.Name("cave_filler_scan")
		@Config.Comment("The amount of RF consumed by cave filler to scan 1 block.")
		@Config.LangKey("config.imhotepmc.energy.cave_filler_scan")
		@Config.RequiresWorldRestart
		public int caveFillerScanCost = 1;

		@Config.Name("cave_filler_fill")
		@Config.Comment("The amount of RF consumed by cave filler to place 1 block.")
		@Config.LangKey("config.imhotepmc.energy.cave_filler_fill")
		@Config.RequiresWorldRestart
		public int caveFillerFillCost = 20;
	}
}
