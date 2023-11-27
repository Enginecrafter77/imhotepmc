package dev.enginecrafter77.imhotepmc.blueprint.iter;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import dev.enginecrafter77.imhotepmc.blueprint.SavedTileState;
import jdk.nashorn.internal.ir.annotations.Immutable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@Immutable
public class ImmutableBlueprintVoxel implements BlueprintVoxel {
	private final BlockPos pos;
	private final BlueprintEntry block;

	public ImmutableBlueprintVoxel(BlockPos pos, BlueprintEntry entry)
	{
		this.pos = pos.toImmutable();
		this.block = SavedTileState.copyOf(entry);
	}

	public ImmutableBlueprintVoxel(BlueprintVoxel copyFrom)
	{
		this(copyFrom.getPosition(), copyFrom);
	}

	@Override
	public BlockPos getPosition()
	{
		return this.pos;
	}

	@Nonnull
	@Override
	public ResourceLocation getBlockName()
	{
		return this.block.getBlockName();
	}

	@Nonnull
	@Override
	public Map<String, String> getBlockProperties()
	{
		return this.block.getBlockProperties();
	}

	@Nullable
	@Override
	public NBTTagCompound getTileEntitySavedData()
	{
		return this.block.getTileEntitySavedData();
	}

	@Nullable
	@Override
	public Block getBlock()
	{
		return this.block.getBlock();
	}

	@Nullable
	@Override
	public IBlockState createBlockState()
	{
		return this.block.createBlockState();
	}

	@Override
	public boolean hasTileEntity()
	{
		return this.block.hasTileEntity();
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull World world)
	{
		return this.block.createTileEntity(world);
	}

	public static ImmutableBlueprintVoxel copyOf(BlueprintVoxel voxel)
	{
		if(voxel instanceof ImmutableBlueprintVoxel)
			return (ImmutableBlueprintVoxel)voxel;
		return new ImmutableBlueprintVoxel(voxel);
	}
}
