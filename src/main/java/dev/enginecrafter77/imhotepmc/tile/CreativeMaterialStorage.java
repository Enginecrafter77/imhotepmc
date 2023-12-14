package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.blueprint.builder.BuilderMaterialStorage;
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
	public boolean canProvide(Collection<ItemStack> items)
	{
		return true;
	}

	@Override
	public boolean canReclaim(Collection<ItemStack> items)
	{
		return true;
	}

	@Override
	public void provide(Collection<ItemStack> items) {}

	@Override
	public void reclaim(Collection<ItemStack> items) {}
}
