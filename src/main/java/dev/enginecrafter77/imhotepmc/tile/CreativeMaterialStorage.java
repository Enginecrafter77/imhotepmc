package dev.enginecrafter77.imhotepmc.tile;

import com.google.common.collect.ImmutableSet;
import dev.enginecrafter77.imhotepmc.blueprint.builder.BuilderMaterialStorage;
import dev.enginecrafter77.imhotepmc.blueprint.builder.ItemStackTransaction;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

public class CreativeMaterialStorage implements BuilderMaterialStorage {
	private final Block templateBlock;

	public CreativeMaterialStorage(Block templateBlock)
	{
		this.templateBlock = templateBlock;
	}

	@Nullable
	@Override
	public Block getAnyAvailableBlock()
	{
		return this.templateBlock;
	}

	@Override
	public ItemStackTransaction consume(Collection<ItemStack> items)
	{
		return new CreativeTransaction(items);
	}

	@Override
	public ItemStackTransaction reclaim(Collection<ItemStack> items)
	{
		return new CreativeTransaction(items);
	}

	public static class CreativeTransaction implements ItemStackTransaction
	{
		private final Collection<ItemStack> items;

		public CreativeTransaction(Collection<ItemStack> items)
		{
			this.items = items;
		}

		@Override
		public Collection<ItemStack> getTransactionStacks()
		{
			return this.items;
		}

		@Override
		public Collection<ItemStack> getBlockingStacks()
		{
			return ImmutableSet.of();
		}

		@Override
		public boolean isCommitable()
		{
			return true;
		}

		@Override
		public void commit() {}
	}
}
