package dev.enginecrafter77.imhotepmc.block;

import cofh.api.block.IDismantleable;
import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TileEntityFluidPump;
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
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.ArrayList;

@Optional.Interface(iface = "cofh.api.block.IDismantleable", modid = "cofhcore")
public class BlockFluidPump extends Block implements IDismantleable {
	public BlockFluidPump()
	{
		super(Material.IRON);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "fluid_pump"));
		this.setTranslationKey("fluid_pump");
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
		return new TileEntityFluidPump();
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
