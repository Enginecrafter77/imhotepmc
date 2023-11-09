package dev.enginecrafter77.imhotepmc.block;

import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockArchitectTable extends Block {
	public BlockArchitectTable()
	{
		super(Material.WOOD);
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
}
