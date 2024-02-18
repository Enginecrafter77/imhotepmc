package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackTransactionView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BuilderTask {
	public BlockPos getPosition();
	public World getWorld();
	public ItemStackTransactionView getItemStackTransaction();
	public int getEnergyRequired();

	public boolean canPerformTask();
	public void performTask();
}
