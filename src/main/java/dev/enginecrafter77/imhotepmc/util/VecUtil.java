package dev.enginecrafter77.imhotepmc.util;

import dev.enginecrafter77.imhotepmc.util.math.Box3d;
import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import dev.enginecrafter77.imhotepmc.util.math.Edge3i;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadableRectangle;

import javax.vecmath.*;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.stream.Stream;

public class VecUtil {
	public static void copyVec3d(Vec3d src, Tuple3d dest)
	{
		dest.set(src.x, src.y, src.z);
	}

	public static void copyVec3d(Vec3i src, Tuple3d dest)
	{
		dest.set(src.getX(), src.getY(), src.getZ());
	}

	public static Vec3i absolute(Vec3i src)
	{
		return new Vec3i(Math.abs(src.getX()), Math.abs(src.getY()), Math.abs(src.getZ()));
	}

	public static Vec3i difference(Vec3i first, Vec3i second)
	{
		return new Vec3i(first.getX() - second.getX(), first.getY() - second.getY(), first.getZ() - second.getZ());
	}

	public static void spriteRect(ReadableDimension area, ReadableRectangle spriteRect, Tuple3d destPos, Tuple2d destSize)
	{
		destPos.x = (double)spriteRect.getX() + (spriteRect.getWidth() / 2D);
		destPos.y = (double)spriteRect.getY() + (spriteRect.getHeight() / 2D);
		destSize.x = spriteRect.getWidth();
		destSize.y = spriteRect.getHeight();

		destPos.x /= area.getWidth();
		destPos.y /= area.getHeight();
		destSize.x /= area.getWidth();
		destSize.y /= area.getHeight();
	}

	public static void facePosition(Tuple3d renderBase, EnumFacing facing, Tuple3d facePos, Tuple3d faceDest)
	{
		faceDest.set(renderBase.x + 0.5D, renderBase.y + 0.5D, renderBase.z + 0.5D);

		Vec3i dir = facing.getDirectionVec();
		faceDest.x += dir.getX() * 0.5D;
		faceDest.y += dir.getY() * 0.5D;
		faceDest.z += dir.getZ() * 0.5D;

		double xoff = facePos.x - 0.5D;
		double yoff = facePos.y - 0.5D;

		switch(facing)
		{
		case UP:
			faceDest.x += xoff;
			faceDest.z += yoff;
			faceDest.y += facePos.z;
			break;
		case DOWN:
			faceDest.x -= xoff;
			faceDest.z -= yoff;
			faceDest.y -= facePos.z;
			break;
		case SOUTH:
			faceDest.x += xoff;
			faceDest.y -= yoff;
			faceDest.z += facePos.z;
			break;
		case NORTH:
			faceDest.x -= xoff;
			faceDest.y -= yoff;
			faceDest.z -= facePos.z;
			break;
		case EAST:
			faceDest.x += facePos.z;
			faceDest.z -= xoff;
			faceDest.y -= yoff;
			break;
		case WEST:
			faceDest.x -= facePos.z;
			faceDest.z += xoff;
			faceDest.y -= yoff;
			break;
		}
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
	public static void interpolateEntityPosition(Entity entity, Tuple3d dest, float partialTicks)
	{
		dest.x = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
		dest.y = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
		dest.z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
	}

	@SideOnly(Side.CLIENT)
	public static void calculateRenderPoint(Entity ent, Tuple3d src, Tuple3d dest, float partialTicks)
	{
		//dest = src - player.pos = -player.pos + src
		interpolateEntityPosition(ent, dest, partialTicks);
		dest.negate();
		dest.add(src);
	}

	public static void boxCenter(Box3d box, Point3d out)
	{
		out.x = (box.start.x + box.end.x) * 0.5D;
		out.y = (box.start.y + box.end.y) * 0.5D;
		out.z = (box.start.z + box.end.z) * 0.5D;
	}

	/**
	 * Sets box parameters so that it covers the area given by an origin point and the selection size.
	 * This method takes precautions to properly handle cases where items of the size vector are negative.
	 * Used to calculate blueprint bounding boxes from schematics.
	 * @param origin The origin point
	 * @param size The size of the enclosed area (can be negative to denote area spanning in axis-negative direction)
	 * @param out The resultant box
	 */
	public static void symbolicAreaToBox(BlockPos origin, Vec3i size, Box3i out)
	{
		out.set(origin, origin);
		if(size.getX() < 0)
			out.start.x += 1;
		if(size.getY() < 0)
			out.start.y += 1;
		if(size.getZ() < 0)
			out.start.z += 1;
		out.setSize(size.getX(), size.getY(), size.getZ());
	}

	public static void boxCoveringBlocks(Iterable<BlockPos> blocks, Box3i out)
	{
		out.set(0, 0, 0, 0, 0, 0);
		Iterator<BlockPos> itr = blocks.iterator();
		if(!itr.hasNext())
			return;
		BlockPos start = itr.next();
		out.set(start.getX(), start.getY(), start.getZ(), start.getX()+1, start.getY()+1, start.getZ()+1);
		while(itr.hasNext())
			out.include(itr.next());
	}

	public static AxisAlignedBB boxToAABB(Box3d box)
	{
		return new AxisAlignedBB(box.start.x, box.start.y, box.start.z, box.end.x, box.end.y, box.end.z);
	}

	public static AxisAlignedBB boxToAABB(Box3i box)
	{
		return new AxisAlignedBB(box.start.x, box.start.y, box.start.z, box.end.x, box.end.y, box.end.z);
	}

	public static Stream<Edge3i> boxDefiningEdges(Box3i box)
	{
		EnumSet<Axis3d> seenAxes = EnumSet.noneOf(Axis3d.class);
		return box.edges()
				.stream()
				.filter(e -> e.length() > 0)
				.filter(e -> seenAxes.add(e.getConnectingAxis()));
	}
}
