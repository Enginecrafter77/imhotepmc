package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.blueprint.builder.BlueprintBuildJob;
import dev.enginecrafter77.imhotepmc.blueprint.builder.BuilderContext;
import dev.enginecrafter77.imhotepmc.blueprint.builder.BuilderPlaceTask;
import dev.enginecrafter77.imhotepmc.net.BuilderSharedStateUpdate;
import dev.enginecrafter77.imhotepmc.render.BlueprintPlacementProvider;
import dev.enginecrafter77.imhotepmc.render.BlueprintPlacementRegistry;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBuilder extends TileEntity implements ITickable, BlueprintPlacementProvider, BuilderContext {
	private static final String NBT_KEY_BLUEPRINT = "blueprint";
	private static final String NBT_KEY_BUILDER = "builder_state";
	private static final String NBT_KEY_FACING = "facing";
	private static final String NBT_KEY_PROJECTION = "projection";
	private static final String NBT_KEY_SHARED = "shared_state";

	private static final Box3i NULL_BOX = new Box3i();

	private static final NBTBlueprintSerializer SERIALIZER = new LitematicaBlueprintSerializer();

	private final EnergyStorage energyStorage;

	@Nullable
	private AxisAlignedBB boundingBox;

	@Nonnull
	private EnumFacing facing;

	@Nullable
	private BlueprintBuildJob job;

	private boolean projectionActive;

	private final SharedBuilderState sharedState;
	private int lastSyncedStateHash;

	public TileEntityBuilder()
	{
		this.energyStorage = new EnergyStorage(16000, 1000, 1000);
		this.facing = EnumFacing.NORTH;
		this.projectionActive = false;
		this.boundingBox = null;
		this.job = null;
		this.sharedState = new SharedBuilderState();
		this.lastSyncedStateHash = 0;
	}

	@Nullable
	@Override
	public IEnergyStorage getEnergyStorage()
	{
		return this.energyStorage;
	}

	@Override
	public IItemHandler getMaterialProvider()
	{
		BlockPos blockSourcePos = this.pos.up();
		TileEntity tile = this.world.getTileEntity(blockSourcePos);
		if(tile == null)
			return EmptyHandler.INSTANCE; // no items
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		if(handler == null)
			return EmptyHandler.INSTANCE; // no items
		return handler;
	}

	public boolean isProjectionActive()
	{
		return this.projectionActive;
	}

	public void setProjectionActive(boolean projectionActive)
	{
		this.projectionActive = projectionActive;
	}

	@Override
	public boolean isPlacementVisible()
	{
		return this.projectionActive;
	}

	public SharedBuilderState getSharedState()
	{
		return this.sharedState;
	}

	public void setFacing(EnumFacing facing)
	{
		this.facing = facing;
	}

	public Box3i getBuildArea()
	{
		if(this.job == null)
			return NULL_BOX;
		return this.job.getPlacement().getBuildAreaBox();
	}

	protected BlueprintPlacement createPlacement(SchematicBlueprint blueprint, BlockPos builderPosition, EnumFacing builderFacing)
	{
		EnumFacing facing = builderFacing.getOpposite();
		BlockPos origin = builderPosition.add(facing.getDirectionVec());
		return BlueprintPlacement.facing(blueprint, origin, facing);
	}

	@Nullable
	@Override
	public BlueprintPlacement getPlacement()
	{
		if(this.job == null)
			return null;
		return this.job.getPlacement();
	}

	@Override
	public long getPlacementProviderUniqueId()
	{
		long sum = 0;
		sum += this.pos.getX();
		sum <<= 21;
		sum += this.pos.getY();
		sum <<= 21;
		sum += this.pos.getZ();
		return sum;
	}

	public void setBlueprint(SchematicBlueprint blueprint)
	{
		BlueprintPlacement placement = this.createPlacement(blueprint, this.pos, this.facing);
		this.job = new BlueprintBuildJob(this, placement);
		this.onJobChanged(this.job);
	}

	protected void onJobChanged(@Nullable BlueprintBuildJob job)
	{
		if(job == null)
		{
			this.boundingBox = new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1D, this.pos.getY() + 1D, this.pos.getZ() + 1D);
			return;
		}
		this.boundingBox = VecUtil.boxToAABB(job.getPlacement().getBuildAreaBox());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		if(this.boundingBox == null)
			return super.getRenderBoundingBox();
		return this.boundingBox;
	}

	@SideOnly(Side.CLIENT)
	public void onStateUpdateReceived(BuilderSharedStateUpdate update)
	{
		this.sharedState.set(update.getState());
	}

	@Override
	public boolean isPlacementValid()
	{
		return !this.tileEntityInvalid;
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		BlueprintPlacementRegistry.proxy.registerProvider(this);
	}

	@Override
	public void update()
	{
		if(this.world.isRemote || this.job == null)
			return;
		this.job.update();

		// bypass item requirements if block above is creative build cache
		BuilderPlaceTask task = (BuilderPlaceTask)this.job.getCurrentTask();
		if(task != null)
		{
			IBlockState above = this.world.getBlockState(this.getPos().up());
			task.setRequireItems(above.getBlock() != ImhotepMod.BLOCK_CREATIVE_BUILD_CACHE);
		}

		this.sharedState.setMissingItemsFrom(this.job.currentlyMissingItems());
		this.sharedState.setPowered(true);

		if(this.lastSyncedStateHash != this.sharedState.hashCode())
		{
			BuilderSharedStateUpdate message = new BuilderSharedStateUpdate(this.getPos(), this.sharedState);
			ImhotepMod.instance.getNetChannel().sendToAll(message);
			this.lastSyncedStateHash = this.sharedState.hashCode();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.facing = EnumFacing.byHorizontalIndex(compound.getByte(NBT_KEY_FACING));
		this.projectionActive = compound.getBoolean(NBT_KEY_PROJECTION);
		boolean jobPresent = compound.hasKey(NBT_KEY_BUILDER) && compound.hasKey(NBT_KEY_BLUEPRINT);
		if(jobPresent)
		{
			SchematicBlueprint blueprint = SERIALIZER.deserializeBlueprint(compound.getCompoundTag(NBT_KEY_BLUEPRINT));
			BlueprintPlacement placement = this.createPlacement(blueprint, this.pos, this.facing);
			this.job = new BlueprintBuildJob(this, placement);
			this.job.restoreState(compound.getCompoundTag(NBT_KEY_BUILDER));
		}
		else
		{
			this.job = null;
		}
		this.onJobChanged(this.job);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setByte(NBT_KEY_FACING, (byte)this.facing.getHorizontalIndex());
		compound.setBoolean(NBT_KEY_PROJECTION, this.projectionActive);

		if(this.job != null)
		{
			SchematicBlueprint blueprint = (SchematicBlueprint)this.job.getPlacement().getBlueprint();
			compound.setTag(NBT_KEY_BLUEPRINT, SERIALIZER.serializeBlueprint(blueprint));
			compound.setTag(NBT_KEY_BUILDER, this.job.saveState());
		}

		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound tag = this.serializeNBT();
		tag.setTag(NBT_KEY_SHARED, this.sharedState.serializeNBT());
		return tag;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		this.sharedState.deserializeNBT(tag.getCompoundTag(NBT_KEY_SHARED));
		super.handleUpdateTag(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
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
