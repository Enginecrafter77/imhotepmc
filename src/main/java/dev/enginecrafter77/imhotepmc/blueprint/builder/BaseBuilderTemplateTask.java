package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class BaseBuilderTemplateTask extends AbstractBuilderPlaceTask {
	public BaseBuilderTemplateTask(World world, BlockPos pos)
	{
		super(world, pos);
	}

	@Nullable
	public abstract Block getBlockToPlace();

	@Nullable
	@Override
	public IBlockState getStateForPlacement()
	{
		return Optional.ofNullable(this.getBlockToPlace()).map(Block::getDefaultState).orElse(null);
	}

	@Nullable
	@Override
	public NBTTagCompound getTileEntityData()
	{
		return null;
	}
}
