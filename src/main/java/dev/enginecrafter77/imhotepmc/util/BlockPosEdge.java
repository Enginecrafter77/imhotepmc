package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import java.util.Objects;

public class BlockPosEdge {
	private final BlockPos p1;
	private final BlockPos p2;
	private final Axis3d axis;

	private BlockPosEdge(Axis3d axis, BlockPos p1, BlockPos p2)
	{
		this.p1 = p1;
		this.p2 = p2;
		this.axis = axis;
	}

	public BlockPos getFirst()
	{
		return this.p1;
	}

	public BlockPos getSecond()
	{
		return this.p2;
	}

	public Axis3d getEdgeAxis()
	{
		return this.axis;
	}

	public int getLength()
	{
		Point3d pt1 = new Point3d();
		Point3d pt2 = new Point3d();

		pt1.set(this.p1.getX(), this.p1.getY(), this.p1.getZ());
		pt2.set(this.p2.getX(), this.p2.getY(), this.p2.getZ());

		double v1 = this.axis.getCoordinationFrom(pt1);
		double v2 = this.axis.getCoordinationFrom(pt2);

		return (int)Math.round(Math.abs(v1 - v2));
	}

	public static BlockPosEdge zero()
	{
		return new BlockPosEdge(Axis3d.X, BlockPos.ORIGIN, BlockPos.ORIGIN);
	}

	public static BlockPosEdge connecting(BlockPos p1, BlockPos p2) throws NotAnEdgeException
	{
		BlockPosEdge edge = tryConnect(p1, p2);
		if(edge == null)
			throw new NotAnEdgeException(p1, p2);
		return edge;
	}

	@Nullable
	public static BlockPosEdge tryConnect(BlockPos p1, BlockPos p2)
	{
		if(Objects.equals(p1, p2))
			return BlockPosEdge.zero();

		Axis3d sharedAxis = BlockPosUtil.getSharedAxis(p1, p2);
		if(sharedAxis == null)
			return null;
		return new BlockPosEdge(sharedAxis, p1, p2);
	}

	public static boolean canBeConnected(BlockPos p1, BlockPos p2)
	{
		return Objects.equals(p1, p2) || BlockPosUtil.getSharedAxis(p1, p2) != null;
	}

	public static class NotAnEdgeException extends Exception
	{
		public NotAnEdgeException(BlockPos p1, BlockPos p2)
		{
			super(String.format("Blocks %s and %s do not make an edge", p1, p2));
		}
	}
}
