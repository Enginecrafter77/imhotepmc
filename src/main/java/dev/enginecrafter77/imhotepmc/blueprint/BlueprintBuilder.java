package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlueprintBuilder {
	public boolean hasNextBlock();
	public void placeNextBlock(World world, BlockPos origin);

	public NBTTagCompound saveState();
	public void restoreState(NBTTagCompound tag);
}
