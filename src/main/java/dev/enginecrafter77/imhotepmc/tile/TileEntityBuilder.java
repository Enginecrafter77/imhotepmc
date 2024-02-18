package dev.enginecrafter77.imhotepmc.tile;

import com.google.common.collect.ImmutableList;
import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.blueprint.builder.*;
import dev.enginecrafter77.imhotepmc.net.BuilderSharedStateUpdate;
import dev.enginecrafter77.imhotepmc.render.BlueprintPlacementProvider;
import dev.enginecrafter77.imhotepmc.render.BlueprintPlacementRegistry;
import dev.enginecrafter77.imhotepmc.util.BlockPosEdge;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackTransactionView;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class TileEntityBuilder extends TileEntity implements ITickable, BlueprintPlacementProvider, BuilderContext {
	private static final String NBT_KEY_BLUEPRINT = "blueprint";
	private static final String NBT_KEY_BUILDER = "builder_state";
	private static final String NBT_KEY_FACING = "facing";
	private static final String NBT_KEY_PROJECTION = "projection";

	private static final NBTBlueprintSerializer SERIALIZER = new LitematicaBlueprintSerializer();

	private final EnergyStorage energyStorage;
	private final BuilderWrapper builderWrapper;

	@Nonnull
	private Collection<BlockPosEdge> buildAreaEdges;

	@Nullable
	private AxisAlignedBB boundingBox;

	@Nonnull
	private EnumFacing facing;

	@Nullable
	private SchematicBlueprint blueprint;

	@Nullable
	private BlueprintPlacement placement;

	private boolean projectionActive;

	@Nonnull
	private final SharedBuilderState sharedState;
	private int lastSyncedStateHash;

	public TileEntityBuilder()
	{
		this.energyStorage = new EnergyStorage(16000, 1000, 1000);
		this.builderWrapper = new BuilderWrapper();
		this.buildAreaEdges = ImmutableList.of();
		this.facing = EnumFacing.NORTH;
		this.projectionActive = false;
		this.boundingBox = null;
		this.blueprint = null;
		this.placement = null;
		this.sharedState = new SharedBuilderState();
		this.lastSyncedStateHash = this.sharedState.hashCode();
	}

	@Nullable
	@Override
	public IEnergyStorage getEnergyStorage()
	{
		return this.energyStorage;
	}

	@Override
	public BuilderBOMProvider getBOMProvider()
	{
		return ImhotepMod.instance.getBuilderBomProvider();
	}

	@Override
	public BuilderMaterialProvider getMaterialProvider()
	{
		return this::getMaterialSource;
	}

	@Override
	public boolean isEnergyRequired()
	{
		return true;
	}

	@Override
	public boolean areItemsRequired()
	{
		BlockPos blockSourcePos = this.pos.up();
		IBlockState state = this.world.getBlockState(blockSourcePos);
		return !Objects.equals(state.getBlock(), ImhotepMod.BLOCK_CREATIVE_BUILD_CACHE);
	}

	@Nullable
	protected IItemHandler getMaterialSource()
	{
		BlockPos blockSourcePos = this.pos.up();
		TileEntity tile = this.world.getTileEntity(blockSourcePos);
		if(tile == null)
			return null;
		if(!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
			return null;
		return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
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

	public Collection<BlockPosEdge> getBuildAreaEdges()
	{
		return this.buildAreaEdges;
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
		return this.placement;
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
		this.blueprint = blueprint;
		this.placement = this.createPlacement(blueprint, this.pos, this.facing);
		BlueprintBuilder builder = new BlueprintBuilder(this.placement, this);
		this.onBuilderCreated(builder);
		this.builderWrapper.setBuilder(builder);
	}

	protected void onBuilderCreated(BlueprintBuilder builder)
	{
		BlockSelectionBox box = new BlockSelectionBox();
		box.setStartSize(builder.getPlacement().getOriginOffset(), builder.getPlacement().getSize());
		this.buildAreaEdges = box.edges();
		box.include(this.getPos());
		this.boundingBox = box.toAABB();
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
		update.exportState(this.sharedState);
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
		if(this.world.isRemote)
			return;

		this.builderWrapper.setWorld(this.world);
		this.builderWrapper.update();

		@Nullable BuilderTask task = this.builderWrapper.getLastTask();
		Collection<ItemStack> missingItems = Optional.ofNullable(task).map(BuilderTask::getItemStackTransaction).map(ItemStackTransactionView::getBlockingStacks).orElseGet(ImmutableList::of);
		this.sharedState.setMissingItems(missingItems);
		this.sharedState.setPowered(true);

		if(this.lastSyncedStateHash != this.sharedState.hashCode())
		{
			BuilderSharedStateUpdate update = new BuilderSharedStateUpdate(this.getPos(), this.sharedState);
			ImhotepMod.instance.getNetChannel().sendToAll(update);
			this.lastSyncedStateHash = this.sharedState.hashCode();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.facing = EnumFacing.byHorizontalIndex(compound.getByte(NBT_KEY_FACING));
		this.projectionActive = compound.getBoolean(NBT_KEY_PROJECTION);

		if(compound.hasKey(NBT_KEY_BLUEPRINT))
			this.blueprint = SERIALIZER.deserializeBlueprint(compound.getCompoundTag(NBT_KEY_BLUEPRINT));

		if(this.blueprint != null)
		{
			this.placement = this.createPlacement(this.blueprint, this.pos, this.facing);
			BlueprintBuilder builder = new BlueprintBuilder(this.placement, this);
			this.builderWrapper.setBuilder(builder);
			this.builderWrapper.restoreState(compound.getCompoundTag(NBT_KEY_BUILDER));
			this.onBuilderCreated(builder);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setByte(NBT_KEY_FACING, (byte)this.facing.getHorizontalIndex());
		compound.setBoolean(NBT_KEY_PROJECTION, this.projectionActive);

		if(this.blueprint != null)
			compound.setTag(NBT_KEY_BLUEPRINT, SERIALIZER.serializeBlueprint(this.blueprint));
		compound.setTag(NBT_KEY_BUILDER, this.builderWrapper.saveState());

		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.serializeNBT();
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
