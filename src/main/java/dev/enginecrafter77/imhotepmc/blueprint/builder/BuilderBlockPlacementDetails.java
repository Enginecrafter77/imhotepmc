package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import javax.annotation.Nullable;

public class BuilderBlockPlacementDetails {
	private final IBlockState baseBlockState;

	@Nullable
	private final NBTTagCompound tileSavedData;

	private final Rotation rotation;

	private final Mirror mirror;

	@Nullable
	private IBlockState transformedState;

	public BuilderBlockPlacementDetails(IBlockState baseBlockState, @Nullable NBTTagCompound tileSavedData, Rotation rotation, Mirror mirror)
	{
		this.baseBlockState = baseBlockState;
		this.tileSavedData = tileSavedData;
		this.rotation = rotation;
		this.mirror = mirror;
		this.transformedState = null;
	}

	public Block getBlock()
	{
		return this.baseBlockState.getBlock();
	}

	@Nullable
	public NBTTagCompound getTileSavedData()
	{
		return this.tileSavedData;
	}

	public Rotation getRotation()
	{
		return this.rotation;
	}

	public Mirror getMirror()
	{
		return this.mirror;
	}

	public IBlockState transformBlockState(IBlockState state)
	{
		state = state.withRotation(this.rotation);
		state = state.withMirror(this.mirror);
		return state;
	}

	public IBlockState getBaseBlockState()
	{
		return this.baseBlockState;
	}

	public IBlockState getTransformedBlockState()
	{
		if(this.transformedState == null)
			this.transformedState = this.transformBlockState(this.baseBlockState);
		return this.transformedState;
	}

	public BuilderBlockPlacementDetails rotated(Rotation rotation)
	{
		return new BuilderBlockPlacementDetails(this.baseBlockState, this.tileSavedData, rotation, this.mirror);
	}

	public BuilderBlockPlacementDetails mirrored(Mirror mirror)
	{
		return new BuilderBlockPlacementDetails(this.baseBlockState, this.tileSavedData, this.rotation, mirror);
	}

	public BuilderBlockPlacementDetails withTileEntityData(@Nullable NBTTagCompound tileSavedData)
	{
		return new BuilderBlockPlacementDetails(this.baseBlockState, tileSavedData, this.rotation, this.mirror);
	}

	public static BuilderBlockPlacementDetails ofBlock(Block block)
	{
		return new BuilderBlockPlacementDetails(block.getDefaultState(), null, Rotation.NONE, Mirror.NONE);
	}

	public static BuilderBlockPlacementDetails fromBlueprintEntry(BlueprintEntry entry)
	{
		IBlockState state = entry.createBlockState();
		if(state == null)
			throw new IllegalStateException();
		return new BuilderBlockPlacementDetails(state, entry.getTileEntitySavedData(), Rotation.NONE, Mirror.NONE);
	}
}
