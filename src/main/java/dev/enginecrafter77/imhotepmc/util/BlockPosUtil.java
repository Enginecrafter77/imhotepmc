package dev.enginecrafter77.imhotepmc.util;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockPosUtil {
	@Nullable
	public static Axis3d getSharedAxis(BlockPos b1, BlockPos b2)
	{
		if(Objects.equals(b1, b2))
			return Axis3d.X;

		if(b1.getX() == b2.getX() && b1.getZ() == b2.getZ())
			return Axis3d.Y;
		if(b1.getX() == b2.getX() && b1.getY() == b2.getY())
			return Axis3d.Z;
		if(b1.getY() == b2.getY() && b1.getZ() == b2.getZ())
			return Axis3d.X;

		return null;
	}

	public static AxisAlignedBB contain(Iterable<BlockPos> itr)
	{
		BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos();
		findBoxMinMax(itr, min, max);
		return new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1D, max.getY() + 1D, max.getZ() + 1D);
	}

	public static Stream<BlockPos> neighbors(BlockPos pos)
	{
		return Stream.of(pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west());
	}

	public static void findBoxMinMax(Iterable<BlockPos> itr, BlockPos.MutableBlockPos outMin, BlockPos.MutableBlockPos outMax)
	{
		Iterator<BlockPos> iterator = itr.iterator();
		if(!iterator.hasNext())
		{
			outMin.setPos(BlockPos.ORIGIN);
			outMax.setPos(BlockPos.ORIGIN);
			return;
		}

		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxZ = Integer.MIN_VALUE;

		while(iterator.hasNext())
		{
			BlockPos blk = iterator.next();
			if(blk.getX() > maxX)
				maxX = blk.getX();
			if(blk.getX() < minX)
				minX = blk.getX();
			if(blk.getY() > maxY)
				maxY = blk.getY();
			if(blk.getY() < minY)
				minY = blk.getY();
			if(blk.getZ() > maxZ)
				maxZ = blk.getZ();
			if(blk.getZ() < minZ)
				minZ = blk.getZ();
		}

		outMin.setPos(minX, minY, minZ);
		outMax.setPos(maxX, maxY, maxZ);
	}

	public static Stream<BlockPos> findCorners(Iterable<BlockPos> range)
	{
		BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos();
		findBoxMinMax(range, min, max);

		BlockPos c1 = new BlockPos(min.getX(), min.getY(), min.getZ());
		BlockPos c2 = new BlockPos(max.getX(), min.getY(), min.getZ());
		BlockPos c3 = new BlockPos(min.getX(), min.getY(), max.getZ());
		BlockPos c4 = new BlockPos(max.getX(), min.getY(), max.getZ());
		BlockPos c5 = new BlockPos(min.getX(), max.getY(), min.getZ());
		BlockPos c6 = new BlockPos(max.getX(), max.getY(), min.getZ());
		BlockPos c7 = new BlockPos(min.getX(), max.getY(), max.getZ());
		BlockPos c8 = new BlockPos(max.getX(), max.getY(), max.getZ());

		return Stream.of(c1, c2, c3, c4, c5, c6, c7, c8);
	}

	public static Collection<BlockPosEdge> findEdges(Iterable<BlockPos> range)
	{
		List<BlockPos> corners = findCorners(range).distinct().collect(Collectors.toList());

		ImmutableList.Builder<BlockPosEdge> edges = ImmutableList.builder();
		for(CombiningIterator.Pair<BlockPos, BlockPos> pos : CombiningIterator.selfCombinations(corners))
		{
			BlockPosEdge edge = BlockPosEdge.tryConnect(pos.getFirst(), pos.getSecond());
			if(edge == null)
				continue;
			if(edge.getLength() == 0)
				continue;
			edges.add(edge);
		}
		return edges.build();
	}

	public static void writeToByteBuf(ByteBuf buf, BlockPos pos)
	{
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

	public static BlockPos readFromByteBuf(ByteBuf buf)
	{
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		return new BlockPos(x, y, z);
	}
}
