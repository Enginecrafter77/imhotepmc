package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.Vec3i;

public class VecUtil {
	public static Vec3i difference(Vec3i first, Vec3i second)
	{
		return new Vec3i(first.getX() - second.getX(), first.getY() - second.getY(), first.getZ() - second.getZ());
	}
}
