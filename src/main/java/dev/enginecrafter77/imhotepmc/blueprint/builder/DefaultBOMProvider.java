package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultBOMProvider implements BuilderBOMProvider {
	private final Map<Block, ItemStack> overrides;

	public DefaultBOMProvider()
	{
		this.overrides = new HashMap<Block, ItemStack>();
	}

	public void addOverride(Block block, ItemStack item)
	{
		this.overrides.put(block, item);
	}

	public void addOverride(Block block, Item item)
	{
		this.addOverride(block, new ItemStack(item));
	}

	@Override
	public Collection<ItemStack> getBlockPlaceRequiredItems(Block block)
	{
		ItemStack stack = this.overrides.get(block);
		if(stack == null)
			stack = new ItemStack(block);
		return Collections.singleton(stack);
	}

	@Override
	public Collection<ItemStack> getBlockClearReclaimedItems(Block block)
	{
		return this.getBlockPlaceRequiredItems(block);
	}
}
