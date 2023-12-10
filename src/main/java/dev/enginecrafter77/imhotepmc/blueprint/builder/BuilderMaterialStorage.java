package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

public interface BuilderMaterialStorage {
	@Nullable
	public Block getAnyAvailableBlock();

	public boolean canProvide(Collection<ItemStack> items);
	public boolean canReclaim(Collection<ItemStack> items);

	public void provide(Collection<ItemStack> items);
	public void reclaim(Collection<ItemStack> items);
}
