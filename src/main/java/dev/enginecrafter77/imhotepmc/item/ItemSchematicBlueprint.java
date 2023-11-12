package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSchematicBlueprint extends Item {
	private static final String SCHEMATIC = "schematic";
	private static final NBTBlueprintSerializer ITEM_SERIALIZER = new LitematicaBlueprintSerializer();

	public static final int META_EMPTY = 0;
	public static final int META_WRITTEN = 1;

	public static ItemSchematicBlueprint INSTANCE = new ItemSchematicBlueprint();

	public ItemSchematicBlueprint()
	{
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "schematic_blueprint"));
		this.setTranslationKey("schematic_blueprint");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Override
	public int getMetadata(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && tag.hasKey(SCHEMATIC) ? META_WRITTEN : META_EMPTY;
	}

	public void setSchematic(ItemStack stack, @Nullable SchematicBlueprint blueprint)
	{
		if(stack.getItem() != INSTANCE)
			throw new UnsupportedOperationException();

		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			tag = new NBTTagCompound();

		if(blueprint == null)
		{
			tag.removeTag(SCHEMATIC);
		}
		else
		{
			tag.setTag(SCHEMATIC, ITEM_SERIALIZER.serializeBlueprint(blueprint));
		}

		stack.setTagCompound(tag);
	}

	@Nullable
	public SchematicBlueprint getSchematic(ItemStack stack)
	{
		if(stack.getItem() != INSTANCE)
			throw new UnsupportedOperationException();

		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return null;

		NBTTagCompound schem = tag.getCompoundTag(SCHEMATIC);
		return ITEM_SERIALIZER.deserializeBlueprint(schem);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);

		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null || !tag.hasKey(SCHEMATIC))
		{
			tooltip.add("No schematic");
			return;
		}

		NBTTagCompound schem = tag.getCompoundTag(SCHEMATIC);
		SchematicBlueprint blueprint = ITEM_SERIALIZER.deserializeBlueprintMetadata(schem);

		tooltip.add("Name: " + blueprint.getName());
		tooltip.add("Description: " + blueprint.getDescription());
		tooltip.add("Author: " + blueprint.getAuthor());
		tooltip.add("Size: " + blueprint.getSize());
		tooltip.add("Region count: " + blueprint.getRegionCount());
	}
}
