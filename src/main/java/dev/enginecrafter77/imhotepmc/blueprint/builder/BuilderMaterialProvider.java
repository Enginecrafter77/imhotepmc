package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface BuilderMaterialProvider {
	@Nullable
	public IItemHandler getBuilderInventory();
}
