package dev.enginecrafter77.imhotepmc.blueprint;

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

public class MutableBlueprintVoxel implements BlueprintVoxel {
	private final BlockPos.MutableBlockPos pos;
	private BlueprintEntry block;

	public MutableBlueprintVoxel()
	{
		this.pos = new BlockPos.MutableBlockPos();
		this.block = null;
	}

	public void set(BlockPos pos, BlueprintEntry block)
	{
		this.pos.setPos(pos);
		this.block = block;
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
}
