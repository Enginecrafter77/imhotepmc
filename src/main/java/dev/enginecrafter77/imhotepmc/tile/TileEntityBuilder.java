package dev.enginecrafter77.imhotepmc.tile;

import com.google.common.collect.ImmutableList;
import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.blueprint.builder.*;
import dev.enginecrafter77.imhotepmc.net.BuilderDwellUpdate;
import dev.enginecrafter77.imhotepmc.util.BlockPosEdge;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.block.Block;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class TileEntityBuilder extends TileEntity implements ITickable {
	private static final String NBT_KEY_BLUEPRINT = "blueprint";
	private static final String NBT_KEY_BUILDER = "builder_state";
	private static final String NBT_KEY_FACING = "facing";

	private static final NBTBlueprintSerializer SERIALIZER = new LitematicaBlueprintSerializer();

	private final EnergyStorage energyStorage;
	private final BuilderHandler builderHandler;
	private final TickedBuilderInvoker builderInvoker;

	@Nonnull
	private Collection<BlockPosEdge> buildAreaEdges;

	@Nullable
	private AxisAlignedBB boundingBox;

	@Nonnull
	private EnumFacing facing;

	@Nullable
	private SchematicBlueprint blueprint;

	@Nullable
	private Block missingBlock;
	private BuilderTask dwellTask;
	private long dwellingTicks;

	public TileEntityBuilder()
	{
		this.energyStorage = new EnergyStorage(16000, 1000, 1000);
		this.builderHandler = new PoweredBuilderHandler(this::getBlockSource, this.energyStorage);
		this.builderInvoker = new TickedBuilderInvoker();
		this.buildAreaEdges = ImmutableList.of();
		this.facing = EnumFacing.NORTH;
		this.boundingBox = null;
		this.blueprint = null;

		this.dwellTask = null;
		this.missingBlock = null;
		this.dwellingTicks = 0;
	}

	public BuilderInvoker getInvoker()
	{
		return this.builderInvoker;
	}

	@Nullable
	public Block getMissingBlock()
	{
		if(this.dwellingTicks < 20)
			return null;
		return this.missingBlock;
	}

	@Nullable
	protected BuilderMaterialStorage getBlockSource()
	{
		BlockPos blockSourcePos = this.pos.up();
		TileEntity tile = this.world.getTileEntity(blockSourcePos);
		if(tile == null)
			return null;
		if(!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
			return null;
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		if(handler == null)
			return null;
		return new InventoryMaterialStorage(handler);
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

	public void setBlueprint(SchematicBlueprint blueprint)
	{
		this.blueprint = blueprint;
		BlueprintPlacement placement = this.createPlacement(blueprint, this.pos, this.facing);
		BlueprintBuilder builder = new BlueprintBuilder(placement, this.builderHandler);
		this.onBuilderCreated(builder);
		this.builderInvoker.setBuilder(builder);
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
	public void onDwellUpdateReceived(BuilderDwellUpdate update)
	{
		this.missingBlock = update.getMissingBlock();
		this.dwellingTicks = update.getDwellingTicks();
	}

	@Override
	public void update()
	{
		if(this.world.isRemote)
			return;

		this.builderInvoker.update(this.world);

		StructureBuilder builder = this.builderInvoker.getBuilder();
		if(builder == null)
			return;
		BuilderTask currentTask = builder.getLastTask(this.world);

		if(currentTask == this.dwellTask)
		{
			++this.dwellingTicks;

			if(this.dwellingTicks == 20)
			{
				BuilderDwellUpdate update = new BuilderDwellUpdate(this.getPos(), this.missingBlock, this.dwellingTicks);
				ImhotepMod.instance.getNetChannel().sendToAll(update);
			}
		}
		else
		{
			this.dwellTask = currentTask;
			this.dwellingTicks = 0;
			this.missingBlock = Optional.ofNullable(currentTask)
					.map(AbstractBuilderPlaceTask.class::cast)
					.map(AbstractBuilderPlaceTask::getStateForPlacement)
					.map(IBlockState::getBlock)
					.orElse(null);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.facing = EnumFacing.byHorizontalIndex(compound.getByte(NBT_KEY_FACING));
		if(compound.hasKey(NBT_KEY_BLUEPRINT))
			this.blueprint = SERIALIZER.deserializeBlueprint(compound.getCompoundTag(NBT_KEY_BLUEPRINT));

		if(this.blueprint != null)
		{
			BlueprintPlacement placement = this.createPlacement(blueprint, this.pos, this.facing);
			BlueprintBuilder builder = new BlueprintBuilder(placement, this.builderHandler);
			if(compound.hasKey(NBT_KEY_BUILDER))
				builder.restoreState(compound.getCompoundTag(NBT_KEY_BUILDER));
			this.onBuilderCreated(builder);
			this.builderInvoker.setBuilder(builder);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setByte(NBT_KEY_FACING, (byte)this.facing.getHorizontalIndex());

		if(this.blueprint != null)
			compound.setTag(NBT_KEY_BLUEPRINT, SERIALIZER.serializeBlueprint(this.blueprint));

		StructureBuilder builder = this.builderInvoker.getBuilder();
		if(builder != null)
			compound.setTag(NBT_KEY_BUILDER, builder.saveState());

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
