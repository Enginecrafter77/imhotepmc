package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TileEntityAreaMarker;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemConstructionTape extends Item {
	private static final String NBT_KEY_LINK = "link";

	public ItemConstructionTape()
	{
		super();
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "construction_tape"));
		this.setTranslationKey("construction_tape");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);

		NBTTagCompound link = stack.getSubCompound(NBT_KEY_LINK);
		if(link == null)
		{
			tooltip.add("Unlinked");
		}
		else
		{
			tooltip.add("Linking: " + NBTUtil.getPosFromTag(link));
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			TileEntity tile = worldIn.getTileEntity(pos);
			if(!(tile instanceof TileEntityAreaMarker))
				return EnumActionResult.FAIL;
			TileEntityAreaMarker marker = (TileEntityAreaMarker)tile;

			ItemStack stack = player.getHeldItem(hand);
			NBTTagCompound tag = stack.getTagCompound();
			if(tag == null)
				tag = new NBTTagCompound();

			NBTTagCompound link = stack.getSubCompound(NBT_KEY_LINK);
			if(link == null)
			{
				link = NBTUtil.createPosTag(pos);
				tag.setTag(NBT_KEY_LINK, link);
			}
			else
			{
				BlockPos linkPos = NBTUtil.getPosFromTag(link);
				if(Objects.equals(linkPos, pos))
					return EnumActionResult.PASS;

				TileEntityAreaMarker other = (TileEntityAreaMarker)worldIn.getTileEntity(linkPos);
				if(other == null)
					return EnumActionResult.FAIL;
				other.tryConnect(marker);

				/*EntityConstructionTape ect = new EntityConstructionTape(worldIn);
				ect.setAnchor(blockCenter(linkPos), blockCenter(pos));
				worldIn.spawnEntity(ect);*/

				tag.removeTag(NBT_KEY_LINK);
			}

			stack.setTagCompound(tag);
		}
		return EnumActionResult.SUCCESS;
	}

	public static Vec3d blockCenter(BlockPos pos)
	{
		return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}
}
