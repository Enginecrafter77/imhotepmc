package dev.enginecrafter77.imhotepmc.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Vector3d;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BlockPosBox {
	public abstract BlockPos getMinCorner();
	public abstract Vec3i getSize();

	public BlockPos getMaxCorner()
	{
		return this.getMinCorner().add(this.getSize()).add(-1, -1, -1);
	}

	public AxisAlignedBB toAABB(BlockAnchor minAnchor, BlockAnchor maxAnchor)
	{
		Vector3d minv = new Vector3d();
		Vector3d maxv = new Vector3d();
		minAnchor.anchorToBlock(this.getMinCorner(), minv);
		maxAnchor.anchorToBlock(this.getMaxCorner(), maxv);
		return new AxisAlignedBB(minv.x, minv.y, minv.z, maxv.x, maxv.y, maxv.z);
	}

	public AxisAlignedBB toAABB()
	{
		return this.toAABB(BlockAnchor.START, BlockAnchor.END);
	}

	public int getVolume()
	{
		Vec3i size = this.getSize();
		return size.getX() * size.getY() * size.getZ();
	}

	public boolean contains(BlockPos pos)
	{
		BlockPos min = this.getMinCorner();
		BlockPos max = this.getMaxCorner();
		
		return pos.getX() >= min.getX() && pos.getX() <= max.getX() &&
				pos.getY() >= min.getY() && pos.getY() <= max.getY() &&
				pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
	}

	private Stream<BlockPos> cornerStream()
	{
		BlockPos min = this.getMinCorner();
		BlockPos max = this.getMaxCorner();

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

	public Collection<BlockPos> corners()
	{
		return this.cornerStream().collect(Collectors.toList());
	}

	public Collection<BlockPosEdge> edges()
	{
		List<BlockPos> corners = this.cornerStream().distinct().collect(Collectors.toList());

		ImmutableList.Builder<BlockPosEdge> edges = ImmutableList.builder();
		for(Pair<BlockPos, BlockPos> pos : CombiningIterator.selfCombinations(corners))
		{
			BlockPosEdge edge = BlockPosEdge.tryConnect(pos.getLeft(), pos.getRight());
			if(edge == null)
				continue;
			if(edge.getLength() == 0)
				continue;
			edges.add(edge);
		}
		return edges.build();
	}
}
