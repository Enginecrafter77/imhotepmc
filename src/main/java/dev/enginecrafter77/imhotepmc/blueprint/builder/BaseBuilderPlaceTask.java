package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BaseBuilderPlaceTask extends AbstractBuilderPlaceTask {
	protected final Block block;

	@Nullable
	protected final NBTTagCompound tileSavedData;

	public BaseBuilderPlaceTask(World world, BlockPos pos, Block block, @Nullable NBTTagCompound tileSavedData)
	{
		super(world, pos);
		this.block = block;
		this.tileSavedData = tileSavedData;
	}

	@Override
	public IBlockState getStateForPlacement()
	{
		return this.block.getDefaultState();
	}

	@Nullable
	@Override
	public NBTTagCompound getTileEntityData()
	{
		return this.tileSavedData;
	}
}
