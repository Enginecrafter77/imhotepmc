package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SavedTileState implements BlueprintEntry {
	private final SavedBlockState state;

	@Nullable
	private final NBTTagCompound tileEntity;

	public SavedTileState(SavedBlockState state, @Nullable NBTTagCompound tileEntity)
	{
		this.tileEntity = tileEntity;
		this.state = state;
	}

	public SavedTileState withProperty(String key, String name)
	{
		return new SavedTileState(this.state.withProperty(key, name), this.tileEntity);
	}

	public <T extends Comparable<T>> SavedTileState withProperty(IProperty<T> prop, T val)
	{
		return new SavedTileState(this.state.withProperty(prop, val), this.tileEntity);
	}

	public <T extends Comparable<T>> Optional<T> getProperty(IProperty<T> prop)
	{
		return this.state.getProperty(prop);
	}

	public SavedTileState withoutProperty(String key)
	{
		return new SavedTileState(this.state.withoutProperty(key), this.tileEntity);
	}

	public SavedTileState withTileEntity(@Nullable NBTTagCompound tileEntity)
	{
		return new SavedTileState(this.state, tileEntity);
	}

	@Override
	public ResourceLocation getBlockName()
	{
		return this.state.getBlockName();
	}

	@Override
	public Map<String, String> getBlockProperties()
	{
		return this.state.getBlockProperties();
	}

	@Nullable
	@Override
	public NBTTagCompound getTileEntitySavedData()
	{
		return this.tileEntity;
	}

	@Nullable
	@Override
	public Block getBlock()
	{
		return this.state.getBlock();
	}

	@Nullable
	@Override
	public IBlockState createBlockState()
	{
		return this.state.createBlockState();
	}

	@Override
	public boolean hasTileEntity()
	{
		return this.tileEntity != null;
	}

	@Nullable
	public NBTTagCompound getTileEntity()
	{
		return this.tileEntity;
	}

	public SavedBlockState getSavedBlockState()
	{
		return this.state;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world)
	{
		if(this.tileEntity == null)
			return null;

		IBlockState state = this.state.createBlockState();
		Block block = state.getBlock();
		if(!block.hasTileEntity(state))
			return null;

		TileEntity tile = block.createTileEntity(world, state);
		if(tile != null)
			tile.deserializeNBT(this.tileEntity);
		return tile;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SavedTileState))
			return false;
		SavedTileState other = (SavedTileState)obj;
		return Objects.equals(this.state, other.state) && Objects.equals(this.tileEntity, other.tileEntity);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.state, this.tileEntity);
	}

	@Override
	public String toString()
	{
		return String.format("%s{%s}", this.state, this.tileEntity);
	}

	public static SavedTileState ofBlock(Block block)
	{
		return SavedTileState.fromBlockState(block.getDefaultState());
	}

	public static SavedTileState fromBlockState(IBlockState state)
	{
		return new SavedTileState(SavedBlockState.fromBlockState(state), null);
	}

	public static SavedTileState sample(IBlockAccess world, BlockPos position)
	{
		SavedBlockState block = SavedBlockState.sample(world, position);
		@Nullable TileEntity tile = world.getTileEntity(position);
		@Nullable NBTTagCompound tileEntityData = Optional.ofNullable(tile).map(TileEntity::serializeNBT).orElse(null);
		return new SavedTileState(block, tileEntityData);
	}

	public static SavedTileState copyOf(BlueprintEntry entry)
	{
		if(entry instanceof SavedTileState)
			return (SavedTileState)entry;
		return new SavedTileState(SavedBlockState.copyOf(entry), entry.getTileEntitySavedData());
	}

	private static SavedTileState AIR_STS = null;
	public static SavedTileState air()
	{
		if(AIR_STS == null)
			AIR_STS = SavedTileState.ofBlock(Blocks.AIR);
		return AIR_STS;
	}
}
