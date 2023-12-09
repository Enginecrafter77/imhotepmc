package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BuilderTask extends ITickable {
	public BlockPos getPosition();
	public World getWorld();
	public boolean isDone();
}
