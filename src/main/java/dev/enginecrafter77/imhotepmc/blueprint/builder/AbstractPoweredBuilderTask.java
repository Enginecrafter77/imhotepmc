package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.energy.EnergyTransaction;
import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackTransaction;
import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackTransactionView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractPoweredBuilderTask implements BuilderTask {
	protected final BuilderContext context;
	protected final World world;
	protected final BlockPos pos;

	@Nullable
	private ItemStackTransaction itemTransaction;

	@Nullable
	private EnergyTransaction energyTransaction;

	public AbstractPoweredBuilderTask(World world, BlockPos pos, BuilderContext context)
	{
		this.context = context;
		this.world = world;
		this.pos = pos;
	}

	protected abstract ItemStackTransaction createItemStackTransaction();
	protected abstract EnergyTransaction createEnergyTransaction();

	@Nonnull
	protected ItemStackTransaction getItemTransaction()
	{
		if(this.itemTransaction == null)
			this.itemTransaction = this.createItemStackTransaction();
		return this.itemTransaction;
	}

	@Nonnull
	protected EnergyTransaction getEnergyTransaction()
	{
		if(this.energyTransaction == null)
			this.energyTransaction = this.createEnergyTransaction();
		return this.energyTransaction;
	}

	@Override
	public BlockPos getPosition()
	{
		return this.pos;
	}

	@Override
	public World getWorld()
	{
		return this.world;
	}

	@Override
	public ItemStackTransactionView getItemStackTransaction()
	{
		return this.getItemTransaction();
	}

	@Override
	public boolean canPerformTask()
	{
		if(this.context.isEnergyRequired())
		{
			IEnergyStorage energyStorage = this.context.getEnergyStorage();
			if(energyStorage == null)
				return false;
			EnergyTransaction energyTransaction = this.getEnergyTransaction();
			energyTransaction.evaluate(energyStorage);
			if(!energyTransaction.canCommit())
				return false;
		}

		if(this.context.areItemsRequired())
		{
			IItemHandler inv = this.context.getMaterialProvider().getBuilderInventory();
			if(inv == null)
				return false;
			ItemStackTransaction itemStackTransaction = this.getItemTransaction();
			itemStackTransaction.evaluate(inv);
			return itemStackTransaction.canCommit();
		}

		return true;
	}

	@Override
	public int getEnergyRequired()
	{
		return this.getEnergyTransaction().getAmount();
	}

	@Override
	public void performTask()
	{
		if(this.context.isEnergyRequired())
			this.getEnergyTransaction().commit();
		if(this.context.areItemsRequired())
			this.getItemTransaction().commit();
	}
}
