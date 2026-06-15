package dev.enginecrafter77.imhotepmc.block;

import cofh.api.block.IDismantleable;
import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TileEntityRadar;
import dev.enginecrafter77.imhotepmc.util.DismantleHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BlockRadar extends Block implements IDismantleable {
	public BlockRadar()
	{
		super(Material.CIRCUITS);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "radar"));
		this.setTranslationKey("radar");
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
		return new TileEntityRadar();
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, boolean returnDrops)
	{
		world.removeTileEntity(pos);
		return DismantleHelper.dismantle(world, pos, returnDrops).drop(this).go();
	}

	@Override
	public boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		return true;
	}
}
