package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.blueprint.builder.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

public class PoweredBuilderHandler implements BuilderHandler {
	private final BuilderMaterialStorageProvider materialStorage;
	private final IEnergyStorage energyStorage;

	public PoweredBuilderHandler(BuilderMaterialStorageProvider storage, IEnergyStorage energyStorage)
	{
		this.materialStorage = storage;
		this.energyStorage = energyStorage;
	}

	public int getEnergyForPlace(World world, BlockPos pos, IBlockState blockState)
	{
		return 100;
	}

	public int getEnergyForBreak(World world, BlockPos pos, IBlockState blockState)
	{
		return Math.max(50, Math.round(200F * blockState.getBlockHardness(world, pos)));
	}

	@Override
	public BuilderTask createPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details)
	{
		return new PoweredPlaceTask(world, pos, details);
	}

	@Override
	public BuilderTask createTemplateTask(World world, BlockPos pos)
	{
		return new PoweredTemplateTask(world, pos);
	}

	@Override
	public BuilderTask createClearTask(World world, BlockPos pos)
	{
		return new PoweredClearTask(world, pos);
	}

	public class PoweredPlaceTask extends MaterializedBuilderPlaceTask
	{
		public PoweredPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details)
		{
			super(world, pos, details, PoweredBuilderHandler.this.materialStorage);
		}

		protected int getEnergyCost()
		{
			IBlockState state = this.getStateForPlacement();
			if(state == null)
				throw new IllegalStateException();
			return PoweredBuilderHandler.this.getEnergyForPlace(this.world, this.pos, state);
		}

		@Override
		public boolean canBeExecuted()
		{
			int required = this.getEnergyCost();
			int extracted = PoweredBuilderHandler.this.energyStorage.extractEnergy(required, true);
			return extracted >= required && super.canBeExecuted();
		}

		@Override
		public void executeTask()
		{
			int required = this.getEnergyCost();
			int extracted = PoweredBuilderHandler.this.energyStorage.extractEnergy(required, true);
			if(extracted < required)
				return;
			PoweredBuilderHandler.this.energyStorage.extractEnergy(required, false);
			super.executeTask();
		}
	}

	public class PoweredTemplateTask extends MaterializedBuilderTemplateTask
	{
		public PoweredTemplateTask(World world, BlockPos pos)
		{
			super(world, pos, PoweredBuilderHandler.this.materialStorage);
		}

		protected int getEnergyCost()
		{
			IBlockState state = this.getStateForPlacement();
			if(state == null)
				throw new IllegalStateException();
			return PoweredBuilderHandler.this.getEnergyForPlace(this.world, this.pos, state);
		}

		@Override
		public boolean canBeExecuted()
		{
			BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
			if(storage == null)
				return false;

			int required = this.getEnergyCost();
			int extracted = PoweredBuilderHandler.this.energyStorage.extractEnergy(required, true);
			return extracted >= required && super.canBeExecuted();
		}

		@Override
		public void executeTask()
		{
			BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
			if(storage == null)
				return;

			int required = this.getEnergyCost();
			int extracted = PoweredBuilderHandler.this.energyStorage.extractEnergy(required, true);
			if(extracted < required)
				return;
			PoweredBuilderHandler.this.energyStorage.extractEnergy(required, false);
			super.executeTask();
		}
	}

	public class PoweredClearTask extends MaterializedBuilderClearTask
	{
		public PoweredClearTask(World world, BlockPos pos)
		{
			super(world, pos, PoweredBuilderHandler.this.materialStorage);
		}

		protected int getEnergyCost()
		{
			IBlockState state = this.world.getBlockState(this.pos);
			return PoweredBuilderHandler.this.getEnergyForBreak(this.world, this.pos, state);
		}

		@Override
		public boolean canBeExecuted()
		{
			int required = this.getEnergyCost();
			int extracted = PoweredBuilderHandler.this.energyStorage.extractEnergy(required, true);
			return extracted >= required && super.canBeExecuted();
		}

		@Override
		public void executeTask()
		{
			int required = this.getEnergyCost();
			int extracted = PoweredBuilderHandler.this.energyStorage.extractEnergy(required, true);
			if(extracted < required)
				return;
			PoweredBuilderHandler.this.energyStorage.extractEnergy(required, false);
			super.executeTask();
		}
	}
}
