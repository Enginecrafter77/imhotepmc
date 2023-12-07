package dev.enginecrafter77.imhotepmc.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Objects;
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
}
