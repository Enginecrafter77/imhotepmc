package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.gui.ImhotepGUIHandler;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBlueprintLibrary;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockBlueprintLibrary extends Block {
	public static final BlockBlueprintLibrary INSTANCE = new BlockBlueprintLibrary();

	public BlockBlueprintLibrary()
	{
		super(Material.WOOD);
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "blueprint_library"));
		this.setTranslationKey("blueprint_library");
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
		return new TileEntityBlueprintLibrary();
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		playerIn.openGui(ImhotepMod.instance, ImhotepGUIHandler.GUI_ID_BLUEPRINT_LIBRARY, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
