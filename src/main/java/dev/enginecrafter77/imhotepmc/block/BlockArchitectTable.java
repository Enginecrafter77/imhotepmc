package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockArchitectTable extends Block {
	public BlockArchitectTable()
	{
		super(Material.WOOD);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "architect_table"));
		this.setTranslationKey("architect_table");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		TileEntityArchitectTable tile = (TileEntityArchitectTable)worldIn.getTileEntity(pos);
		if(tile == null)
			return;

		if(!tile.isInitialized())
			return;

		BlockSelectionBox box = new BlockSelectionBox();
		tile.getArea(box);

		BlockPos start = box.getStart();
		Vec3i size = box.getSize();

		for(int index = 0; index < 32; ++index)
		{
			double px = start.getX() + size.getX() * worldIn.rand.nextDouble();
			double py = start.getY() + size.getY() * worldIn.rand.nextDouble();
			double pz = start.getZ() + size.getZ() * worldIn.rand.nextDouble();
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, 0D, 0D, 0D);
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntityArchitectTable createTileEntity(World world, IBlockState state)
	{
		return new TileEntityArchitectTable();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(hand != EnumHand.MAIN_HAND)
			return false;

		ItemStack stack = playerIn.getHeldItem(hand);
		if(stack.getItem() != ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT)
			return false;

		TileEntityArchitectTable tile = (TileEntityArchitectTable)worldIn.getTileEntity(pos);
		if(tile == null)
			return false;

		SchematicBlueprint blueprint = tile.sample();
		ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT.setSchematic(stack, blueprint);
		return true;
	}
}
