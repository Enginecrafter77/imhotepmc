package dev.enginecrafter77.imhotepmc.marker;

import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.UUID;

public interface MarkedArea {
	public UUID getId();
	public Box3i getMarkedAreaBox();
	public Collection<BlockPos> getDefiningMembers();
}
