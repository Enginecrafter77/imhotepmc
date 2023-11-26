package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.AreaMarkGroup;
import dev.enginecrafter77.imhotepmc.tile.TileEntityAreaMarker;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockAreaMarker extends Block {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.4D, 0.0D, 0.4D, 0.6D, 0.6D, 0.6D); // +y
	private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.4D, 0.4D, 0.4D, 0.6D, 1D, 0.6D); // -y
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.4D, 0.4D, 0D, 0.6D, 0.6D, 0.6D); // +z
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.4D, 0.4D, 0.6D, 0.6D, 0.6D, 1D); // -z
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0D, 0.4D, 0.4D, 0.6D, 0.6D, 0.6D); // +x
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.4D, 0.4D, 0.4D, 1D, 0.6D, 0.6D); // -x

	public BlockAreaMarker()
	{
		super(Material.CIRCUITS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "area_marker"));
		this.setTranslationKey("area_marker");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getIndex();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		switch(state.getValue(FACING))
		{
		default:
		case UP:
			return UP_AABB;
		case DOWN:
			return DOWN_AABB;
		case NORTH:
			return NORTH_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case EAST:
			return EAST_AABB;
		case WEST:
			return WEST_AABB;
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
	}

	public EnumFacing getAnchor(IBlockState state)
	{
		return state.getValue(FACING).getOpposite();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntityAreaMarker marker = (TileEntityAreaMarker)worldIn.getTileEntity(pos);
		if(marker != null)
		{
			AreaMarkGroup group = marker.getCurrentMarkGroup();
			ItemStack tape = new ItemStack(ImhotepMod.ITEM_CONSTRUCTION_TAPE, group.getUsedTapeCount());
			EntityItem item = new EntityItem(worldIn);
			item.setItem(tape);
			item.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			worldIn.spawnEntity(item);
			group.dismantle(worldIn, TileEntityAreaMarker::getMarkerFromTile);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
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
		return new TileEntityAreaMarker();
	}
}
