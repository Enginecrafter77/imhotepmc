package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.energy.EnergyExtractTransaction;
import dev.enginecrafter77.imhotepmc.util.energy.EnergyTransaction;
import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackExtractTransaction;
import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackTransaction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BuilderPlaceTask extends AbstractPoweredBuilderTask {
	private final BuilderBlockPlacementDetails placementDetails;
	private final IBlockState blockState;

	@Nullable
	private final TileEntity tile;

	protected int updateFlags;

	public BuilderPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details, BuilderContext context)
	{
		super(world, pos, context);
		this.placementDetails = details;
		this.blockState = details.getTransformedBlockState();
		this.tile = createTileEntity(world, this.blockState, details.getTileSavedData());
		this.updateFlags = 3;
	}

	@Override
	protected ItemStackTransaction createItemStackTransaction()
	{
		return new ItemStackExtractTransaction(this.context.getBOMProvider().getBlockPlaceRequiredItems(this.world, this.pos, this.blockState, this.tile));
	}

	@Override
	protected EnergyTransaction createEnergyTransaction()
	{
		return new EnergyExtractTransaction(1000);
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
		return this.blockState;
	}

	@Nullable
	public TileEntity getTileEntity()
	{
		return this.tile;
	}

	@Override
	public void performTask()
	{
		super.performTask();
		this.world.setBlockState(this.pos, this.blockState, this.updateFlags);
		if(this.tile != null)
			this.world.setTileEntity(this.pos, this.tile);
	}

	@Nullable
	private static TileEntity createTileEntity(World world, @Nullable IBlockState state, @Nullable NBTTagCompound tileSavedData)
	{
		if(state == null)
			return null;
		Block block = state.getBlock();

		if(!block.hasTileEntity(state))
			return null;

		TileEntity tileEntity = block.createTileEntity(world, state);
		if(tileEntity != null && tileSavedData != null)
			tileEntity.deserializeNBT(tileSavedData);
		return tileEntity;
	}
}
