package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.block.BlockBuilder;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintBuilder;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class TileEntityBuilder extends TileEntity {
	private static final NBTBlueprintSerializer SERIALIZER = new LitematicaBlueprintSerializer();

	@Nullable
	private SchematicBlueprint blueprint;

	@Nullable
	private BlueprintBuilder builder;

	public TileEntityBuilder()
	{
		this.blueprint = null;
		this.builder = null;
	}

	public void setBlueprint(SchematicBlueprint blueprint)
	{
		this.blueprint = blueprint;
		this.builder = blueprint.schematicBuilder();
	}

	public boolean builderStep()
	{
		if(this.builder == null)
			return false;

		if(!this.builder.hasNextBlock())
			return false;

		IBlockState state = this.world.getBlockState(this.getPos());
		EnumFacing facing = state.getValue(BlockBuilder.FACING);
		BlockPos origin = this.getPos().add(facing.getOpposite().getDirectionVec()); // block behind
		this.builder.placeNextBlock(this.world, origin);
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		boolean set = compound.getBoolean("set");
		if(!set)
		{
			this.blueprint = null;
			this.builder = null;
			return;
		}

		this.blueprint = SERIALIZER.deserializeBlueprint(compound.getCompoundTag("blueprint"));
		this.builder = this.blueprint.schematicBuilder();
		this.builder.restoreState(compound.getCompoundTag("builder_state"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setBoolean("set", this.blueprint != null);

		if(this.blueprint == null || this.builder == null)
			return compound;

		compound.setTag("blueprint", SERIALIZER.serializeBlueprint(this.blueprint));
		compound.setTag("builder_state", this.builder.saveState());
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.serializeNBT();
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		this.deserializeNBT(tag);
	}
}
