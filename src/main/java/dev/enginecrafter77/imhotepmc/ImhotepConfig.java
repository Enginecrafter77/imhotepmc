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
		@Config.LangKey("config.imhotepmc.general.cave_filler_max_blocks")
		@Config.RangeInt(min = 1)
		public int caveFillerMaxBlocks = 262144;

		@Config.Name("cave_filler_scan_rate")
		@Config.Comment("The average number of blocks scanned per tick by the cave filler.")
		@Config.LangKey("config.imhotepmc.general.cave_filler_scan_rate")
		@Config.RangeDouble(min = 0.0625)
		@Config.RequiresWorldRestart
		public double caveFillerScanRate = 64.0D;

		@Config.Name("cave_filler_fill_rate")
		@Config.Comment("The average number of blocks filled per tick by the cave filler.")
		@Config.LangKey("config.imhotepmc.general.cave_filler_fill_rate")
		@Config.RangeDouble(min = 0.0625)
		@Config.RequiresWorldRestart
		public double caveFillerFillRate = 8.0D;
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
