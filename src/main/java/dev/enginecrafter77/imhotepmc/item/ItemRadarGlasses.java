package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ItemRadarGlasses extends Item {
	public ItemRadarGlasses()
	{
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "radar_glasses"));
		this.setTranslationKey("radar_glasses");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Nullable
	@Override
	public EntityEquipmentSlot getEquipmentSlot(ItemStack stack)
	{
		return EntityEquipmentSlot.HEAD;
	}
}
