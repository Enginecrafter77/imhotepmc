package dev.enginecrafter77.imhotepmc.tile;

import com.google.common.collect.ImmutableList;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintBuilder;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.util.BlockPosEdge;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class TileEntityBuilder extends TileEntity implements ITickable {
	private static final Vec3i VEC_ONE = new Vec3i(1, 1, 1);

	private static final int ENERGY_PER_BLOCK = 100;
	private static final int MAX_BLOCKS_PER_TICK = 8;

	private static final NBTBlueprintSerializer SERIALIZER = new LitematicaBlueprintSerializer();

	private final EnergyStorage energyStorage;

	@Nullable
	private SchematicBlueprint blueprint;

	@Nonnull
	private Collection<BlockPosEdge> buildAreaEdges;

	@Nullable
	private AxisAlignedBB boundingBox;

	@Nullable
	private BlueprintBuilder builder;

	@Nonnull
	private Vec3i buildOriginOffset;

	private long tickTime;
	private long lastBuildTick;

	public TileEntityBuilder()
	{
		this.energyStorage = new EnergyStorage(16000, 1000, 0);
		this.buildAreaEdges = ImmutableList.of();
		this.buildOriginOffset = Vec3i.NULL_VECTOR;
		this.boundingBox = null;
		this.blueprint = null;
		this.builder = null;
		this.tickTime = 0L;
		this.lastBuildTick = 0L;
	}

	public void setBuildOriginOffset(Vec3i origin)
	{
		this.buildOriginOffset = origin;
	}

	public BlockPos getBuildOrigin()
	{
		return this.getPos().add(this.buildOriginOffset);
	}

	public Collection<BlockPosEdge> getBuildAreaEdges()
	{
		return this.buildAreaEdges;
	}

	public void setBlueprint(SchematicBlueprint blueprint)
	{
		this.blueprint = blueprint;
		this.builder = new BlueprintBuilder(blueprint);
		this.onBlueprintChanged(blueprint);
	}

	protected void onBlueprintChanged(SchematicBlueprint blueprint)
	{
		List<BlockPos> corners = ImmutableList.of(this.getBuildOrigin(), this.getBuildOrigin().add(blueprint.getSize()).subtract(VEC_ONE));
		this.buildAreaEdges = BlockPosUtil.findEdges(corners);
		this.boundingBox = BlockPosUtil.contain(ImmutableList.<BlockPos>builder().addAll(corners).add(this.getPos()).build());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		if(this.boundingBox == null)
			return super.getRenderBoundingBox();
		return this.boundingBox;
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

		this.builder.setOrigin(this.getBuildOrigin());
		this.builder.setWorld(this.world);
		if(!this.builder.hasNextBlock())
			return false;

		this.builder.placeNextBlock();
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
		this.builder = new BlueprintBuilder(this.blueprint);
		this.builder.restoreState(compound.getCompoundTag("builder_state"));
		this.onBlueprintChanged(this.blueprint);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
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
