package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public class VecUtil {
	public static void copyVec3d(Vec3d src, Tuple3d dest)
	{
		dest.set(src.x, src.y, src.z);
	}

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

	public static void midpoint(Tuple3d v1, Tuple3d v2, Tuple3d mid)
	{
		mid.set(v1);
		mid.add(v2);
		mid.scale(0.5D);
	}

	@SideOnly(Side.CLIENT)
	public static void interpolatePlayerPosition(EntityPlayerSP playerSP, Tuple3d dest, float partialTicks)
	{
		dest.x = playerSP.prevPosX + (playerSP.posX - playerSP.prevPosX) * partialTicks;
		dest.y = playerSP.prevPosY + (playerSP.posY - playerSP.prevPosY) * partialTicks;
		dest.z = playerSP.prevPosZ + (playerSP.posZ - playerSP.prevPosZ) * partialTicks;
	}

	@SideOnly(Side.CLIENT)
	public static void calculateRenderPoint(EntityPlayerSP player, Tuple3d src, Tuple3d dest, float partialTicks)
	{
		//dest = src - player.pos = -player.pos + src
		interpolatePlayerPosition(player, dest, partialTicks);
		dest.negate();
		dest.add(src);
	}
}
