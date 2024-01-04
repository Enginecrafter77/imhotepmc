package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

public interface BuilderMaterialStorage {
	@Nullable
	public Block getAnyAvailableBlock();

	public ItemStackTransaction consume(Collection<ItemStack> items);
	public ItemStackTransaction reclaim(Collection<ItemStack> items);
}
