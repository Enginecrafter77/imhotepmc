package dev.enginecrafter77.imhotepmc.tile;

import com.google.common.base.Predicates;
import dev.enginecrafter77.imhotepmc.shape.*;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class TileEntityTerraformer extends TileEntity implements ITickable {
	private static final String NBT_KEY_AREA = "area";
	private static final String NBT_KEY_MODE = "mode";
	private static final String NBT_KEY_HAS_AREA = "hasArea";
	private static final String NBT_KEY_STATE = "state";

	private final EnergyStorage energyStorage;

	private final BlockSelectionBox selectionBox;

	private final BuilderHandler builderHandler;

	private boolean hasArea;

	@Nonnull
	private TerraformMode mode;

	@Nullable
	private ShapeBuilder builder;

	public TileEntityTerraformer()
	{
		this.energyStorage = new EnergyStorage(16000, 1000, 1000);
		this.selectionBox = new BlockSelectionBox();
		this.builderHandler = new TileBuilderHandler(new ItemHandlerDelegate(this::getBlockSource), this.energyStorage);

		this.mode = TerraformMode.CLEAR;
		this.hasArea = false;
		this.builder = null;
	}

	public void setMode(TerraformMode mode)
	{
		this.mode = mode;
		this.onSettingsChanged(this.selectionBox, mode);
	}

	@Nullable
	protected IItemHandler getBlockSource()
	{
		BlockPos blockSourcePos = this.pos.up();
		TileEntity tile = this.world.getTileEntity(blockSourcePos);
		if(tile == null)
			return null;
		if(!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
			return null;
		return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
	}

	protected void onSettingsChanged(BlockSelectionBox box, TerraformMode mode)
	{
		this.builder = new ShapeBuilder(box, mode.getShapeGenerator(), mode.getBuildStrategy(), this.builderHandler);
	}

	@Override
	public void update()
	{
		if(this.world.isRemote)
			return;

		if(!this.hasArea)
		{
			AreaMarkGroup group = BlockPosUtil.neighbors(this.getPos())
					.map(this.world::getTileEntity)
					.filter(Objects::nonNull)
					.filter(Predicates.instanceOf(IAreaMarker.class))
					.map(IAreaMarker.class::cast)
					.map(IAreaMarker::getCurrentMarkGroup)
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);
			if(group != null)
			{
				group.select(this.selectionBox);
				group.dismantle(this.world);
				group.dropTapes(this.world);
				for(BlockPos corner : group.getDefiningCorners())
					this.world.destroyBlock(corner, true);
				this.hasArea = true;

				this.onSettingsChanged(this.selectionBox, this.mode);
			}
		}

		if(this.builder == null)
			return;

		if(!this.builder.isReady() || this.builder.isFinished())
			return;
		this.builder.tryPlaceNextBlock(this.world);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.selectionBox.deserializeNBT(compound.getCompoundTag(NBT_KEY_AREA));
		this.mode = TerraformMode.values()[compound.getByte(NBT_KEY_MODE)];
		this.hasArea = compound.getBoolean(NBT_KEY_HAS_AREA);
		if(compound.hasKey(NBT_KEY_STATE))
		{
			this.builder = new ShapeBuilder(this.selectionBox, this.mode.getShapeGenerator(), this.mode.getBuildStrategy(), this.builderHandler);
			this.builder.restoreState(compound.getCompoundTag(NBT_KEY_STATE));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag(NBT_KEY_AREA, this.selectionBox.serializeNBT());
		compound.setByte(NBT_KEY_MODE, (byte)this.mode.ordinal());
		compound.setBoolean(NBT_KEY_HAS_AREA, this.hasArea);
		if(this.builder != null)
			compound.setTag(NBT_KEY_STATE, this.builder.saveState());
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
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

	public static enum TerraformMode
	{
		CLEAR(ShapeGenerator.clear(), ShapeBuildStrategy.TOP_DOWN),
		FILL(ShapeGenerator.fill(), ShapeBuildStrategy.BOTTOM_UP);

		private final ShapeGenerator shapeGenerator;
		private final ShapeBuildStrategy buildStrategy;

		private TerraformMode(ShapeGenerator generator, ShapeBuildStrategy strategy)
		{
			this.shapeGenerator = generator;
			this.buildStrategy = strategy;
		}

		public ShapeGenerator getShapeGenerator()
		{
			return this.shapeGenerator;
		}

		public ShapeBuildStrategy getBuildStrategy()
		{
			return this.buildStrategy;
		}
	}
}