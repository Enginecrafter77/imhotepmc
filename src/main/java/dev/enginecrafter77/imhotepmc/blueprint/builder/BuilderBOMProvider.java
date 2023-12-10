package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface BuilderBOMProvider {
	public Collection<ItemStack> getBlockPlaceRequiredItems(Block block);
	public Collection<ItemStack> getBlockClearReclaimedItems(Block block);
}
