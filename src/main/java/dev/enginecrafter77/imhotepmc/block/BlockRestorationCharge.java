package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.entity.EntityPrimedRestorationCharge;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockRestorationCharge extends Block {
	public BlockRestorationCharge()
	{
		super(Material.TNT);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "restoration_charge"));
		this.setTranslationKey("restoration_charge");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TextComponentTranslation("desc.restoration_charge.0").getFormattedText());
		tooltip.add(new TextComponentTranslation("desc.restoration_charge.1").getFormattedText());
		tooltip.add(new TextComponentTranslation("desc.restoration_charge.2").getFormattedText());
		tooltip.add(new TextComponentTranslation("desc.restoration_charge.3").getFormattedText());
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		if(worldIn.isBlockPowered(pos))
			this.trigger(worldIn, pos);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = playerIn.getHeldItem(hand);
		if(stack.getItem() == Items.FLINT_AND_STEEL)
		{
			stack.damageItem(1, playerIn);
			this.trigger(worldIn, pos);
			return true;
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	protected void trigger(World worldIn, BlockPos pos)
	{
		worldIn.setBlockToAir(pos);
		if(!worldIn.isRemote)
		{
			EntityPrimedRestorationCharge entity = new EntityPrimedRestorationCharge(worldIn);
			entity.setExplosionRadius(4D);
			entity.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			worldIn.spawnEntity(entity);
		}
	}
}
