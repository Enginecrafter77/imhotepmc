package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BaseBuilderPlaceTask extends AbstractBuilderPlaceTask {
	protected final BuilderBlockPlacementDetails details;

	public BaseBuilderPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details)
	{
		super(world, pos);
		this.details = details;
	}

	@Override
	public IBlockState getStateForPlacement()
	{
		return this.details.getTransformedBlockState();
	}

	@Nullable
	@Override
	public NBTTagCompound getTileEntityData()
	{
		return this.details.getTileSavedData();
	}
}
