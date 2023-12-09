package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBuilder extends Block {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockBuilder()
	{
		super(Material.CIRCUITS);
		this.setRegistryName(ImhotepMod.MOD_ID, "builder");
		this.setTranslationKey("builder");
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
	public TileEntityBuilder createTileEntity(World world, IBlockState state)
	{
		EnumFacing facing = state.getValue(BlockBuilder.FACING);
		TileEntityBuilder builder = new TileEntityBuilder();
		builder.setFacing(facing);
		return builder;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(hand != EnumHand.MAIN_HAND)
			return false;

		TileEntityBuilder tile = (TileEntityBuilder)worldIn.getTileEntity(pos);
		if(tile == null)
			return false;

		ItemStack item = playerIn.getHeldItem(hand);
		if(item.getItem() == ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT)
		{
			SchematicBlueprint blueprint = ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT.getSchematic(item);
			if(blueprint == null)
				return false;
			tile.setBlueprint(blueprint);
			return true;
		}

		return false;
	}
}
