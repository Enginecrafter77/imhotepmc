package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;

import javax.annotation.Nullable;

public interface BuilderMaterialStorage {
	@Nullable
	public Block getAnyAvailableBlock();

	public boolean canProvide(Block block);
	public boolean canReclaim(Block block);

	public void provide(Block block);
	public void reclaim(Block block);
}
