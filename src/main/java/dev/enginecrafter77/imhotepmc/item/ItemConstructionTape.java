package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemConstructionTape extends Item {
	public ItemConstructionTape()
	{
		super();
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "construction_tape"));
		this.setTranslationKey("construction_tape");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}
}
