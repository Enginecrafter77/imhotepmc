package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface BuilderMaterialStorage {
	@Nullable
	public Block getAnyAvailableBlock();

	public boolean hasBlock(Block block);
	public boolean canInsert(ItemStack stack);

	public ItemStack consumeBlock(Block block);
	public void addBlockDrops(ItemStack drop);
}
