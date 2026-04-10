package dev.enginecrafter77.imhotepmc.util.transaction;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class ItemStackTransactionTemplate {
	public static final ItemStackTransactionTemplate EMPTY = new ItemStackTransactionTemplate(Collections.emptyList(), Collections.emptyList());

	private final Collection<ItemStack> consumed;
	private final Collection<ItemStack> recovered;

	public ItemStackTransactionTemplate(Collection<ItemStack> consumed, Collection<ItemStack> recovered)
	{
		this.consumed = Collections.unmodifiableCollection(consumed);
		this.recovered = Collections.unmodifiableCollection(recovered);
	}

	public Collection<ItemStack> getConsumedItems()
	{
		return this.consumed;
	}

	public Collection<ItemStack> getRecoveredItems()
	{
		return this.recovered;
	}

	public boolean isEmpty()
	{
		return this.consumed.isEmpty() && this.recovered.isEmpty();
	}

	public static ItemStackTransactionBuilder builder()
	{
		return new ItemStackTransactionBuilder();
	}

	public static class ItemStackTransactionBuilder {
		private final ImmutableList.Builder<ItemStack> consumed;
		private final ImmutableList.Builder<ItemStack> recovered;

		public ItemStackTransactionBuilder()
		{
			this.consumed = ImmutableList.builder();
			this.recovered = ImmutableList.builder();
		}

		public ItemStackTransactionBuilder consume(ItemStack... stacks)
		{
			this.consumed.add(stacks);
			return this;
		}

		public ItemStackTransactionBuilder consumeAll(Iterable<ItemStack> stacks)
		{
			this.consumed.addAll(stacks);
			return this;
		}

		public ItemStackTransactionBuilder recover(ItemStack... stacks)
		{
			this.recovered.add(stacks);
			return this;
		}

		public ItemStackTransactionBuilder recoverAll(Iterable<ItemStack> stacks)
		{
			this.recovered.addAll(stacks);
			return this;
		}

		public ItemStackTransactionTemplate build()
		{
			return new ItemStackTransactionTemplate(this.consumed.build(), this.recovered.build());
		}
	}
}
