package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TileEntityCaveFiller;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCaveFiller extends Block {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockCaveFiller()
	{
		super(Material.IRON);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "cave_filler"));
		this.setTranslationKey("cave_filler");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);

		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		EnumFacing facing = placer.getAdjustedHorizontalFacing().getOpposite();
		state = state.withProperty(FACING, facing);
		worldIn.setBlockState(pos, state, 2);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		return state.withProperty(FACING, mirrorIn.mirror(state.getValue(FACING)));
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
			tile.reset();
			return true;
		}

		ItemStack item = playerIn.getHeldItem(hand);
		if(item.isEmpty())
		{
			if(!worldIn.isRemote)
			{
				playerIn.sendMessage(new TextComponentString(String.format("State: %s, Cave model blocks: %d, Filled blocks: %d", tile.getState().name(), tile.getCaveModel().size(), tile.getFilledBlocks())));
			}
		}
		return true;
	}
}
