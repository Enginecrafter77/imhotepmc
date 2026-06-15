package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ItemRadarGlasses extends ItemArmor {
	public ItemRadarGlasses()
	{
		super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "radar_glasses"));
		this.setTranslationKey("radar_glasses");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Nullable
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		return ImhotepMod.MOD_ID + ":textures/models/armor/radar_glasses.png";
	}
}
