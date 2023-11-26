package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBuilder extends Block {
	public BlockBuilder()
	{
		super(Material.CIRCUITS);
		this.setRegistryName(ImhotepMod.MOD_ID, "builder");
		this.setTranslationKey("builder");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
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
		return new TileEntityBuilder();
	}
}
