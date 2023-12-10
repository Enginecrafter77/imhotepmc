package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class BlockMachineHull extends Block {
	public BlockMachineHull()
	{
		super(Material.CIRCUITS);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "machine_hull"));
		this.setTranslationKey("machine_hull");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}
}
