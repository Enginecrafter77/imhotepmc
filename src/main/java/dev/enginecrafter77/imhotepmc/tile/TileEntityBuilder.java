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
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nullable;

public class TileEntityBuilder extends TileEntity implements ITickable {
	private static final int ENERGY_PER_BLOCK = 100;
	private static final int MAX_BLOCKS_PER_TICK = 8;

	private static final NBTBlueprintSerializer SERIALIZER = new LitematicaBlueprintSerializer();

	private final EnergyStorage energyStorage;

	@Nullable
	private SchematicBlueprint blueprint;

	@Nullable
	private BlueprintBuilder builder;

	private long tickTime;
	private long lastBuildTick;

	public TileEntityBuilder()
	{
		this.energyStorage = new EnergyStorage(16000, 1000, 0);
		this.blueprint = null;
		this.builder = null;
		this.tickTime = 0L;
		this.lastBuildTick = 0L;
	}

	public void setBlueprint(SchematicBlueprint blueprint)
	{
		this.blueprint = blueprint;
		this.builder = blueprint.schematicBuilder();
	}

	@Override
	public void update()
	{
		if(this.blueprint == null || this.builder == null)
			return;

		// We require redstone power
		if(!this.world.isBlockPowered(this.getPos()))
			return;

		if(this.energyStorage.getEnergyStored() < ENERGY_PER_BLOCK)
			return;

		float fill = (float)this.energyStorage.getEnergyStored() / (float)this.energyStorage.getMaxEnergyStored();
		float bpt = MAX_BLOCKS_PER_TICK * fill;
		int delay = Math.round(1F / bpt);

		long time = this.tickTime++;
		int elapsedSince = (int)(time - this.lastBuildTick);
		if(elapsedSince < delay)
			return;
		this.lastBuildTick = time;

		int bptR = Math.max(Math.round(bpt), 1);
		for(int index = 0; index < bptR; ++index)
		{
			if(!this.builderStep())
				return;
			this.energyStorage.extractEnergy(ENERGY_PER_BLOCK, false);
		}
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

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
			return true;
		return super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
			return CapabilityEnergy.ENERGY.cast(this.energyStorage);
		return super.getCapability(capability, facing);
	}
}
