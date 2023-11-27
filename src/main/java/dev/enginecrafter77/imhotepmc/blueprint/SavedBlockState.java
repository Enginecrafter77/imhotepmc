package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SavedBlockState implements BlueprintEntry {
	private final ResourceLocation name;

	private final Map<String, String> blockProps;

	public SavedBlockState(ResourceLocation name, Map<String, String> blockProps)
	{
		this.name = name;
		this.blockProps = blockProps;
	}

	public SavedBlockState withProperties(Map<String, String> properties)
	{
		return new SavedBlockState(this.name, properties);
	}

	public SavedBlockState withProperty(String key, String val)
	{
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for(Map.Entry<String, String> propEntry : this.blockProps.entrySet())
		{
			String value = propEntry.getValue();
			if(Objects.equals(propEntry.getKey(), key))
				value = val;
			builder.put(propEntry.getKey(), value);
		}
		return this.withProperties(builder.build());
	}

	public <T extends Comparable<T>> SavedBlockState withProperty(IProperty<T> prop, T val)
	{
		return this.withProperty(prop.getName(), prop.getName(val));
	}

	public SavedBlockState withoutProperty(String key)
	{
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for(Map.Entry<String, String> propEntry : this.blockProps.entrySet())
		{
			if(!Objects.equals(propEntry.getKey(), key))
				builder.put(propEntry.getKey(), propEntry.getValue());
		}
		return this.withProperties(builder.build());
	}

	public String getProperty(String key)
	{
		return this.blockProps.get(key);
	}

	public <T extends Comparable<T>> Optional<T> getProperty(@Nonnull IProperty<T> prop)
	{
		return prop.parseValue(this.getProperty(prop.getName())).toJavaUtil();
	}

	@Override
	public ResourceLocation getBlockName()
	{
		return this.name;
	}

	@Override
	public Map<String, String> getBlockProperties()
	{
		return this.blockProps;
	}

	@Nullable
	@Override
	public NBTTagCompound getTileEntitySavedData()
	{
		return null;
	}

	@Nullable
	@Override
	public Block getBlock()
	{
		return Block.REGISTRY.getObject(this.name);
	}

	@Nullable
	public IBlockState createBlockState()
	{
		Block blk = this.getBlock();
		if(blk == null)
			return null;

		IBlockState state = blk.getDefaultState();
		BlockStateContainer container = blk.getBlockState();

		for(Map.Entry<String, String> entry : this.blockProps.entrySet())
		{
			IProperty<?> prop = container.getProperty(entry.getKey());
			if(prop == null)
				continue;
			state = SavedBlockState.mutateBlockState(prop, state, entry.getValue());
		}

		return state;
	}

	@Override
	public boolean hasTileEntity()
	{
		return false;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world)
	{
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SavedBlockState))
			return false;
		SavedBlockState other = (SavedBlockState)obj;
		return Objects.equals(this.name, other.name) && Objects.equals(this.blockProps, other.blockProps);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.name, this.blockProps);
	}

	@Override
	public String toString()
	{
		return String.format("%s[%s]", this.name.toString(), this.blockProps);
	}

	public NBTTagCompound serialize()
	{
		NBTTagCompound entryTag = new NBTTagCompound();
		entryTag.setString("Name", this.getBlockName().toString());
		NBTTagCompound propsTag = new NBTTagCompound();
		this.getBlockProperties().forEach(propsTag::setString);
		entryTag.setTag("Properties", propsTag);
		return entryTag;
	}

	public static SavedBlockState deserialize(NBTTagCompound tag)
	{
		ResourceLocation name = new ResourceLocation(tag.getString("Name"));
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		NBTTagCompound props = tag.getCompoundTag("Properties");
		for(String key : props.getKeySet())
		{
			String val = props.getString(key);
			builder.put(key, val);
		}
		return new SavedBlockState(name, builder.build());
	}

	public static SavedBlockState ofBlock(Block block)
	{
		return SavedBlockState.fromBlockState(block.getDefaultState());
	}

	public static SavedBlockState fromBlockState(IBlockState state)
	{
		Block block = state.getBlock();
		BlockStateContainer container = block.getBlockState();
		ResourceLocation blockName = block.getRegistryName();
		if(blockName == null)
			throw new IllegalArgumentException("Cannot serialize block with no registry name!");
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for(IProperty<?> prop : container.getProperties())
			builder.put(SavedBlockState.serializeProperty(prop, state));
		return new SavedBlockState(blockName, builder.build());
	}

	public static SavedBlockState sample(IBlockAccess world, BlockPos position)
	{
		return SavedBlockState.fromBlockState(world.getBlockState(position));
	}

	public static SavedBlockState copyOf(BlueprintEntry entry)
	{
		if(entry instanceof SavedBlockState)
			return (SavedBlockState)entry;
		if(entry instanceof SavedTileState)
			return ((SavedTileState)entry).getSavedBlockState();
		return new SavedBlockState(entry.getBlockName(), entry.getBlockProperties());
	}

	private static <T extends Comparable<T>> Map.Entry<String, String> serializeProperty(IProperty<T> prop, IBlockState state)
	{
		T value = state.getValue(prop);
		String valName = prop.getName(value);
		return new AbstractMap.SimpleImmutableEntry<String, String>(prop.getName(), valName);
	}

	private static <T extends Comparable<T>> IBlockState mutateBlockState(IProperty<T> property, IBlockState state, String value)
	{
		Optional<T> val = property.parseValue(value).toJavaUtil();
		if(val.isPresent())
			state = state.withProperty(property, val.get());
		return state;
	}
}
