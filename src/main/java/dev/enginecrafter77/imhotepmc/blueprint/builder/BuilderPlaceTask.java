package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.util.transaction.ItemStackTransactionTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class BuilderPlaceTask extends AbstractBuilderTask {
	private final BuilderBlockPlacementDetails placementDetails;

	protected int updateFlags;

	public BuilderPlaceTask(BuilderContext context, BlockPos pos, BuilderBlockPlacementDetails details)
	{
		super(context, pos);
		this.placementDetails = details;
		this.updateFlags = 3;
	}

	@Override
	public ItemStackTransactionTemplate createItemStackTransactionTemplate()
	{
		return ItemStackTransactionTemplate.builder()
				.consumeAll(ImhotepMod.instance.getBuilderBomProvider().getBlockPlaceRequiredItems(this.getWorld(), this.pos, this.getBlockState(), this.getWorld().getTileEntity(this.pos)))
				.build();
	}

	@Override
	public int getEnergyRequired()
	{
		return 1000;
	}

	public void setUpdateFlags(int updateFlags)
	{
		this.updateFlags = updateFlags;
	}

	public BuilderBlockPlacementDetails getPlacementDetails()
	{
		return this.placementDetails;
	}

	public IBlockState getBlockState()
	{
		return this.placementDetails.getTransformedBlockState();
	}

	@Override
	public void performTask()
	{
		this.getWorld().setBlockState(this.pos, this.getBlockState(), this.updateFlags);
		TileEntity tile = this.createTileEntity();
		if(tile != null)
			this.getWorld().setTileEntity(this.pos, tile);
	}

	@Nullable
	private TileEntity createTileEntity()
	{
		IBlockState state = this.getBlockState();
		Block block = state.getBlock();

		if(!block.hasTileEntity(state))
			return null;

		NBTTagCompound tileSavedData = this.placementDetails.getTileSavedData();
		TileEntity tileEntity = block.createTileEntity(this.getWorld(), state);
		if(tileEntity != null && tileSavedData != null)
			tileEntity.deserializeNBT(tileSavedData);
		return tileEntity;
	}
}
