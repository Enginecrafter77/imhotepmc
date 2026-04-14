package dev.enginecrafter77.imhotepmc.util;

import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import dev.enginecrafter77.imhotepmc.util.math.Edge3i;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class BlockPosUtil {
	@Nullable
	public static Axis3d getSharedAxis(BlockPos b1, BlockPos b2)
	{
		return Edge3i.getConnectingEdgeAxis(b1.getX(), b1.getY(), b1.getZ(), b2.getX(), b2.getY(), b2.getZ());
	}

	public static Stream<BlockPos> neighbors(BlockPos pos)
	{
		return Stream.of(pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west());
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

	public static Iterable<BlockPos.MutableBlockPos> blocksInBox(Box3i box)
	{
		return BlockPos.getAllInBoxMutable(box.start.x, box.start.y, box.start.z, box.end.x-1, box.end.y-1, box.end.z-1);
	}
}
