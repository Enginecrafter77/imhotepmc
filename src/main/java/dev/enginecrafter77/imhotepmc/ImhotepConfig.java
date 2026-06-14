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

		@Config.Name("builder_place_rate")
		@Config.Comment("The average number of blocks placed by builder per tick.")
		@Config.LangKey("config.imhotepmc.general.cave_filler_fill_rate")
		@Config.RangeDouble(min = 0.0625)
		@Config.RequiresWorldRestart
		public double builderPlaceRate = 8.0D;

		@Config.Name("fluid_pump_rate")
		@Config.Comment("The average number of liquid blocks pumped by the fluid pump per tick.")
		@Config.LangKey("config.imhotepmc.general.fluid_pump_rate")
		@Config.RangeDouble(min = 0.0625)
		@Config.RequiresWorldRestart
		public double fluidPumpRate = 2.0D;
	}

	public static class EnergyCosts {
		@Config.Name("cave_filler_scan_cost")
		@Config.Comment("The amount of RF consumed by cave filler to scan 1 block.")
		@Config.LangKey("config.imhotepmc.energy.cave_filler_scan_cost")
		@Config.RequiresWorldRestart
		public int caveFillerScanCost = 1;

		@Config.Name("cave_filler_fill_cost")
		@Config.Comment("The amount of RF consumed by cave filler to place 1 block.")
		@Config.LangKey("config.imhotepmc.energy.cave_filler_fill_cost")
		@Config.RequiresWorldRestart
		public int caveFillerFillCost = 20;

		@Config.Name("terraformer_place_cost")
		@Config.Comment("The amount of RF consumed by the terraformer to place 1 block.")
		@Config.LangKey("config.imhotepmc.energy.terraformer_place_cost")
		public int terraformerPlaceCost = 100;

		@Config.Name("terraformer_clear_cost")
		@Config.Comment("The base amount of RF consumed by the terraformer to break 1 block.")
		@Config.LangKey("config.imhotepmc.energy.terraformer_clear_cost")
		public double terraformerClearCost = 400;

		@Config.Name("terraformer_clear_hardness_factor")
		@Config.Comment("The amount of RF consumed per block hardness point by the terraformer to break 1 said block.")
		@Config.LangKey("config.imhotepmc.energy.terraformer_clear_hardness_factor")
		public double terraformerClearHardnessFactor = 200;

		@Config.Name("builder_place_cost")
		@Config.Comment("The amount of RF consumed by the builder to place 1 block.")
		@Config.LangKey("config.imhotepmc.energy.builder_place_cost")
		public int builderPlaceCost = 100;
	}
}
