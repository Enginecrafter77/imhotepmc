package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class ResolvedBlueprintBlock {
	private final IBlockState state;

	@Nullable
	private final NBTTagCompound tileEntitySavedData;

	public ResolvedBlueprintBlock(IBlockState state, @Nullable NBTTagCompound tileEntitySavedData)
	{
		this.state = state;
		this.tileEntitySavedData = tileEntitySavedData;
	}

	public IBlockState getBlockState()
	{
		return this.state;
	}

	@Nullable
	public TileEntity createTileEntity(World world)
	{
		if(this.tileEntitySavedData == null)
			return null;

		IBlockState state = this.state;
		Block block = state.getBlock();
		if(!block.hasTileEntity(state))
			return null;

		TileEntity tile = block.createTileEntity(world, state);
		if(tile != null)
			tile.deserializeNBT(this.tileEntitySavedData);
		return tile;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.state, this.tileEntitySavedData);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ResolvedBlueprintBlock))
			return false;
		ResolvedBlueprintBlock other = (ResolvedBlueprintBlock)obj;

		return Objects.equals(this.state, other.state) && Objects.equals(this.tileEntitySavedData, other.tileEntitySavedData);
	}

	public SavedTileState save()
	{
		return SavedTileState.fromBlockState(this.state).withTileEntity(this.tileEntitySavedData);
	}

	public static ResolvedBlueprintBlock from(SavedTileState savedTileState)
	{
		return new ResolvedBlueprintBlock(savedTileState.getSavedBlockState().createBlockState(), savedTileState.getTileEntity());
	}
}
