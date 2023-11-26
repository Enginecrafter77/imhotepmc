package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSchematicBlueprint extends Item {
	private static final String SCHEMATIC = "schematic";
	private static final NBTBlueprintSerializer ITEM_SERIALIZER = new LitematicaBlueprintSerializer();

	public static final int META_EMPTY = 0;
	public static final int META_WRITTEN = 1;

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
		if(!(stack.getItem() instanceof ItemSchematicBlueprint))
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
		if(!(stack.getItem() instanceof ItemSchematicBlueprint))
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
		SchematicMetadata meta = ITEM_SERIALIZER.deserializeBlueprintMetadata(schem);

		tooltip.add("Name: " + meta.getName());
		tooltip.add("Description: " + meta.getDescription());
		tooltip.add("Author: " + meta.getAuthor());
		tooltip.add("Size: " + sizeVectorToString(meta.getSize()));
		tooltip.add("Region count: " + meta.getRegionCount());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		SchematicBlueprint blueprint = this.getSchematic(stack);
		if(blueprint == null)
			return EnumActionResult.PASS;

		BlockPos origin = pos.up();
		BlueprintBuilder builder = blueprint.schematicBuilder();
		while(builder.hasNextBlock())
			builder.placeNextBlock(worldIn, origin);

		return EnumActionResult.SUCCESS;
	}

	private static String sizeVectorToString(Vec3i vector)
	{
		return String.format("%dx%dx%d", vector.getX(), vector.getY(), vector.getZ());
	}
}
