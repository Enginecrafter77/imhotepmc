package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TerraformMode;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ItemShapeCard extends Item {
	public ItemShapeCard()
	{
		super();
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "shape_card"));
		this.setTranslationKey("shape_card");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	public TerraformMode getMode(ItemStack stack)
	{
		return TerraformMode.values()[stack.getMetadata()];
	}

	public ItemStack stackFromMode(TerraformMode mode)
	{
		return new ItemStack(this, 1, mode.ordinal());
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		TerraformMode mode = this.getMode(stack);
		ITextComponent label = new TextComponentTranslation(this.getTranslationKey(stack) + ".name").appendText(": ").appendSibling(mode.getTranslatedName());
		return label.getFormattedText();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(!this.isInCreativeTab(tab))
			return;

		for(TerraformMode mode : TerraformMode.values())
			items.add(new ItemStack(this, 1, mode.ordinal()));
	}
}
