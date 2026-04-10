package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.transaction.EnergyConsumeTransaction;
import dev.enginecrafter77.imhotepmc.util.transaction.ItemStackTransaction;
import dev.enginecrafter77.imhotepmc.util.transaction.ItemStackTransactionTemplate;
import dev.enginecrafter77.imhotepmc.util.transaction.Transaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public abstract class AbstractBuilderTask implements BuilderTask {
	protected final BuilderContext context;
	protected final BlockPos pos;

	@Nullable
	private ItemStackTransaction itemTransaction; // cached itemTransaction

	@Nullable
	private IBlockState propertyBlockState; // used for caching itemTransaction

	public AbstractBuilderTask(BuilderContext context, BlockPos pos)
	{
		this.propertyBlockState = null;
		this.context = context;
		this.pos = pos;
	}

	public abstract ItemStackTransactionTemplate createItemStackTransactionTemplate();
	public abstract int getEnergyRequired();
	public abstract void performTask();

	public Stream<ItemStack> missingItems()
	{
		if(this.itemTransaction == null)
			return Stream.empty();
		return this.itemTransaction.mismatchedConsumes();
	}

	/**
	 * Called when the block this task affects is changed (change is checked every tick in {@link #update()}).
	 */
	protected void onTargetBlockChanged()
	{
		this.itemTransaction = new ItemStackTransaction(this.createItemStackTransactionTemplate());
	}

	@Override
	public BlockPos getPosition()
	{
		return this.pos;
	}

	@Override
	public World getWorld()
	{
		return this.context.getWorld();
	}

	@Override
	public Transaction asTransaction()
	{
		if(this.itemTransaction == null)
			this.itemTransaction = new ItemStackTransaction(this.createItemStackTransactionTemplate());
		this.itemTransaction.setSource(this.context.getMaterialProvider());
		this.itemTransaction.setDestination(this.context.getMaterialProvider());

		IEnergyStorage storage = context.getEnergyStorage();
		Transaction energy = new EnergyConsumeTransaction(storage, getEnergyRequired());
		Transaction action = new BuilderActionTransaction();
		return Transaction.compose(energy, this.itemTransaction, action);
	}

	@Override
	public void update()
	{
		IBlockState state = this.getWorld().getBlockState(this.pos);
		if(state != this.propertyBlockState)
		{
			this.onTargetBlockChanged();
			this.propertyBlockState = state;
		}
	}

	public class BuilderActionTransaction implements Transaction {
		@Override
		public boolean canCommit()
		{
			return true;
		}

		@Override
		public void commit()
		{
			AbstractBuilderTask.this.performTask();
		}
	}
}
