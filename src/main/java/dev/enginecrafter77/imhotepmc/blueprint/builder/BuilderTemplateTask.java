package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.transaction.EnergyConsumeTransaction;
import dev.enginecrafter77.imhotepmc.util.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BuilderTemplateTask implements BuilderTask {
	private final BuilderContext context;
	private final BlockPos pos;
	protected int updateFlags;

	public BuilderTemplateTask(BuilderContext context, BlockPos pos)
	{
		this.context = context;
		this.pos = pos;
		this.updateFlags = 3;
	}

	public void setUpdateFlags(int updateFlags)
	{
		this.updateFlags = updateFlags;
	}

	public BuilderContext getContext()
	{
		return this.context;
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
		return new TemplatedPlaceTransaction();
	}

	@Override
	public void update()
	{
		//NOOP
	}

	void placeBlock(IBlockState state)
	{
		this.getWorld().setBlockState(this.pos, state, this.updateFlags);
	}

	private static class TemplateMaterialMatch
	{
		private final IItemHandler source;
		private final IBlockState state;
		private final int slot;

		public TemplateMaterialMatch(IItemHandler source, IBlockState state, int slot)
		{
			this.source = source;
			this.state = state;
			this.slot = slot;
		}

		public IBlockState getBlockState()
		{
			return this.state;
		}

		public void consume()
		{
			this.source.extractItem(this.slot, 1, false);
		}

		@Nullable
		public static TemplateMaterialMatch tryMatch(IItemHandler source, int slot)
		{
			ItemStack stack = source.getStackInSlot(slot);
			Item item = stack.getItem();
			if(!(item instanceof ItemBlock))
				return null;
			Block blk = ((ItemBlock)item).getBlock();
			@SuppressWarnings("deprecation") IBlockState state = blk.getStateFromMeta(stack.getMetadata());
			if(!state.isFullCube())
				return null;
			return new TemplateMaterialMatch(source, state, slot);
		}
	}

	public class TemplatedPlaceTransaction implements Transaction
	{
		private final EnergyConsumeTransaction energyTransaction;

		@Nullable
		private TemplateMaterialMatch material;

		public TemplatedPlaceTransaction()
		{
			this.energyTransaction = new EnergyConsumeTransaction(BuilderTemplateTask.this.getContext().getEnergyStorage(), 1000);
			this.material = null;
		}

		private void findMaterial()
		{
			if(this.material != null)
				return;

			IItemHandler inv = getContext().getMaterialProvider();
			for(int i = 0; i < inv.getSlots(); ++i)
			{
				this.material = TemplateMaterialMatch.tryMatch(inv, i);
				if(this.material != null)
					break;
			}
		}

		public void invalidate()
		{
			this.material = null;
		}

		@Override
		public boolean canCommit()
		{
			if(!this.energyTransaction.canCommit())
				return false;
			this.findMaterial();
			return this.material != null;
		}

		@Override
		public void commit()
		{
			this.findMaterial();
			assert this.material != null;

			this.energyTransaction.commit();
			this.material.consume();
			BuilderTemplateTask.this.placeBlock(this.material.getBlockState());
		}
	}
}
