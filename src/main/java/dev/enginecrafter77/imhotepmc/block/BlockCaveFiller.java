package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TileEntityCaveFiller;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCaveFiller extends Block {
	public BlockCaveFiller()
	{
		super(Material.IRON);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "cave_filler"));
		this.setTranslationKey("cave_filler");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityCaveFiller();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntityCaveFiller tile = (TileEntityCaveFiller)worldIn.getTileEntity(pos);
		if(tile == null)
			return false;
		if(playerIn.isSneaking())
		{
			tile.stop();
			return true;
		}

		ItemStack item = playerIn.getHeldItem(hand);
		if(item.isEmpty())
		{
			tile.setActive(!tile.isActive());
		}
		else if(item.getItem() == Items.STICK)
		{
			tile.scan();
		}
		else if(item.getItem() == Items.PAPER)
		{
			if(!worldIn.isRemote)
			{
				playerIn.sendMessage(new TextComponentString(String.format("State: %s, Cave model blocks: %d, Filled blocks: %d", tile.getState().name(), tile.getCaveModel().size(), tile.getFilledBlocks())));
			}
		}
		else if(item.getItem() instanceof ItemBlock)
		{
			Block blk = ((ItemBlock)item.getItem()).getBlock();
			tile.setFillBlock(blk.getDefaultState());
			tile.fill();
		}
		return true;
	}
}
