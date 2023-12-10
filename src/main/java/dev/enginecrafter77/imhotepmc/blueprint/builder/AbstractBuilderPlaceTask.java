package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AbstractBuilderPlaceTask extends AbstractBuilderTask {
	protected int updateFlags;

	public AbstractBuilderPlaceTask(World world, BlockPos pos)
	{
		super(world, pos);
		this.updateFlags = 3;
	}

	@Nullable
	public abstract IBlockState getStateForPlacement();

	@Nullable
	public abstract NBTTagCompound getTileEntityData();

	@Nullable
	public TileEntity createTileEntity()
	{
		IBlockState state = this.getStateForPlacement();
		if(state == null)
			return null;
		Block block = state.getBlock();

		if(!block.hasTileEntity(state))
			return null;

		TileEntity tileEntity = block.createTileEntity(this.world, state);
		NBTTagCompound tileSavedData = this.getTileEntityData();
		if(tileEntity != null && tileSavedData != null)
			tileEntity.deserializeNBT(tileSavedData);
		return tileEntity;
	}

	@Override
	public boolean canBeExecuted()
	{
		return this.getStateForPlacement() != null;
	}

	@Override
	public void executeTask()
	{
		IBlockState state = this.getStateForPlacement();
		if(state == null)
			return;

		this.world.setBlockState(this.pos, state, this.updateFlags);
		TileEntity tileEntity = this.createTileEntity();
		if(tileEntity != null)
			this.world.setTileEntity(this.pos, tileEntity);
	}
}
