package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class StructureBlockSavedData implements INBTSerializable<NBTTagCompound> {
	private static final Log LOGGER = LogFactory.getLog(StructureBlockSavedData.class);

	private static final String NBT_KEY_BLOCKSTATE = "block_state";
	private static final String NBT_KEY_TILEENTITY = "tile_entity";
	private static final String NBT_KEY_TILEENTITY_EXISTS = "tile_entity";

	private IBlockState blockState;

	@Nullable
	private NBTTagCompound tileEntity;

	public StructureBlockSavedData(IBlockState blockState, @Nullable NBTTagCompound tileEntity)
	{
		this.blockState = blockState;
		this.tileEntity = tileEntity;
	}

	public void setBlockState(IBlockState state)
	{
		this.blockState = state;
	}

	public void setTileEntity(@Nullable NBTTagCompound tileEntityData)
	{
		this.tileEntity = tileEntityData;
	}

	public boolean hasTileEntity()
	{
		return this.tileEntity != null;
	}

	@Nullable
	public NBTTagCompound getTileEntity()
	{
		return this.tileEntity;
	}

	public IBlockState getBlockState()
	{
		return this.blockState;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof StructureBlockSavedData))
			return false;
		StructureBlockSavedData other = (StructureBlockSavedData)obj;
		return Objects.equals(this.blockState, other.blockState) && Objects.equals(this.tileEntity, other.tileEntity);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.blockState, this.tileEntity);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();

		NBTTagCompound state = NBTUtil.writeBlockState(new NBTTagCompound(), this.blockState);
		compound.setTag(NBT_KEY_BLOCKSTATE, state);

		if(this.tileEntity != null)
		{
			compound.setBoolean(NBT_KEY_TILEENTITY_EXISTS, true);
			compound.setTag(NBT_KEY_TILEENTITY, this.tileEntity);
		}
		else
		{
			compound.setBoolean(NBT_KEY_TILEENTITY_EXISTS, false);
		}
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		NBTTagCompound state = nbt.getCompoundTag(NBT_KEY_BLOCKSTATE);
		this.blockState = NBTUtil.readBlockState(state);

		this.tileEntity = null;
		boolean tileEntityPresent = nbt.getBoolean(NBT_KEY_TILEENTITY_EXISTS);
		if(tileEntityPresent)
			this.tileEntity = nbt.getCompoundTag(NBT_KEY_TILEENTITY);
	}

	public void insert(World world, BlockPos position)
	{
		world.setBlockState(position, this.blockState, 3);

		if(this.tileEntity != null)
		{
			TileEntity entity = this.blockState.getBlock().createTileEntity(world, this.blockState);
			if(entity == null)
			{
				LOGGER.error("Attempting to set tile entity for block with no implicit tile entity: " + this.blockState.getBlock().getRegistryName());
				return;
			}
			world.setTileEntity(position, entity);
		}
	}

	public static StructureBlockSavedData sample(IBlockAccess world, BlockPos position)
	{
		IBlockState state = world.getBlockState(position);
		@Nullable TileEntity tile = world.getTileEntity(position);
		@Nullable NBTTagCompound tileEntityData = Optional.ofNullable(tile).map(TileEntity::serializeNBT).orElse(null);

		return new StructureBlockSavedData(state, tileEntityData);
	}
}
