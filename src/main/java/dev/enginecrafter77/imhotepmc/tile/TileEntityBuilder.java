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
	private static final String NBT_KEY_BLUEPRINT = "blueprint";
	private static final String NBT_KEY_BUILDER = "builder_state";
	private static final String NBT_KEY_FACING = "facing";

	private static final Vec3i VEC_ONE = new Vec3i(1, 1, 1);

	private static final int ENERGY_PER_BLOCK = 100;
	private static final int MAX_BLOCKS_PER_TICK = 8;

	private static final NBTBlueprintSerializer SERIALIZER = new LitematicaBlueprintSerializer();

	private final EnergyStorage energyStorage;

	@Nonnull
	private Collection<BlockPosEdge> buildAreaEdges;

	@Nullable
	private AxisAlignedBB boundingBox;

	@Nullable
	private BlueprintBuilder builder;

	@Nonnull
	private EnumFacing facing;

	private long tickTime;
	private long lastBuildTick;

	public TileEntityBuilder()
	{
		this.energyStorage = new EnergyStorage(16000, 1000, 0);
		this.buildAreaEdges = ImmutableList.of();
		this.facing = EnumFacing.NORTH;
		this.boundingBox = null;
		this.builder = null;
		this.tickTime = 0L;
		this.lastBuildTick = 0L;
	}

	public void setFacing(EnumFacing facing)
	{
		this.facing = facing;
	}

	public BlockPos getBuildOrigin()
	{
		return this.getPos().add(this.facing.getOpposite().getDirectionVec());
	}

	public Collection<BlockPosEdge> getBuildAreaEdges()
	{
		return this.buildAreaEdges;
	}

	public void setBlueprint(SchematicBlueprint blueprint)
	{
		this.builder = this.createBuilder(blueprint);
		this.onBuilderCreated(this.builder);
	}

	protected BlueprintBuilder createBuilder(SchematicBlueprint blueprint)
	{
		BlueprintBuilder builder = new BlueprintBuilder(blueprint);
		builder.setRotationFromFacing(this.facing.getOpposite());
		return builder;
	}

	protected void onBuilderCreated(BlueprintBuilder builder)
	{
		BlockPos first = this.getBuildOrigin();
		BlockPos last = this.getBuildOrigin().add(builder.getBuildSize()).subtract(VEC_ONE);
		List<BlockPos> corners = ImmutableList.of(first, last);
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
		if(this.builder == null)
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
		this.facing = EnumFacing.byHorizontalIndex(compound.getByte(NBT_KEY_FACING));
		if(!compound.hasKey(NBT_KEY_BLUEPRINT))
			return;

		SchematicBlueprint blueprint = SERIALIZER.deserializeBlueprint(compound.getCompoundTag(NBT_KEY_BLUEPRINT));
		this.builder = this.createBuilder(blueprint);
		this.builder.restoreState(compound.getCompoundTag(NBT_KEY_BUILDER));
		this.onBuilderCreated(this.builder);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setByte(NBT_KEY_FACING, (byte)this.facing.getHorizontalIndex());
		if(this.builder == null)
			return compound;

		compound.setTag(NBT_KEY_BLUEPRINT, SERIALIZER.serializeBlueprint(this.builder.getBlueprint()));
		compound.setTag(NBT_KEY_BUILDER, this.builder.saveState());
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
