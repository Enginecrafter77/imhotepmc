package dev.enginecrafter77.imhotepmc.shape;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface BuilderHost {
	@Nullable
	public Block getAvailableBlock();

	public boolean onPlaceBlock(World world, BlockPos pos, IBlockState newBlockState);
	public boolean onClearBlock(World world, BlockPos pos, IBlockState oldBlockState);
}
