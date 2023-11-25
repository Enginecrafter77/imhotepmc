package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class VecUtil {
	public static Vec3i difference(Vec3i first, Vec3i second)
	{
		return new Vec3i(first.getX() - second.getX(), first.getY() - second.getY(), first.getZ() - second.getZ());
	}

	public static AxisAlignedBB transform(AxisAlignedBB box, Matrix4d matrix)
	{
		Vector3d boxStart = new Vector3d(box.minX, box.minY, box.minZ);
		Vector3d boxEnd = new Vector3d(box.maxX, box.maxY, box.maxZ);
		matrix.transform(boxStart);
		matrix.transform(boxEnd);
		return new AxisAlignedBB(boxStart.x, boxStart.y, boxStart.z, boxEnd.x, boxEnd.y, boxEnd.z);
	}
}
