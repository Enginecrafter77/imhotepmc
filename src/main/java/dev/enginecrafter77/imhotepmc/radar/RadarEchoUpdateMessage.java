package dev.enginecrafter77.imhotepmc.radar;

import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import dev.enginecrafter77.imhotepmc.util.FastBlockPosSet;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RadarEchoUpdateMessage implements IMessage {
	private final FastBlockPosSet blocks;
	private BlockPos origin;

	public RadarEchoUpdateMessage()
	{
		this.blocks = new FastBlockPosSet();
		this.origin = BlockPos.ORIGIN;
	}

	public RadarEchoUpdateMessage(BlockPos origin, FastBlockPosSet blocks)
	{
		this.blocks = blocks;
		this.origin = origin;
	}

	public FastBlockPosSet getBlocks()
	{
		return this.blocks;
	}

	public BlockPos getOrigin()
	{
		return this.origin;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.origin = BlockPosUtil.readFromByteBuf(buf);
		int len = buf.readInt();
		int[] arr = new int[len];
		for(int i = 0; i < len; ++i)
			arr[i] = buf.readInt();
		this.blocks.fromIntArray(arr);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		BlockPosUtil.writeToByteBuf(buf, this.origin);
		int[] arr = this.blocks.toIntArray();
		buf.writeInt(arr.length);
		for(int j : arr)
			buf.writeInt(j);
	}

	public static RadarEchoUpdateMessage update(BlockPos origin, FastBlockPosSet blocks)
	{
		return new RadarEchoUpdateMessage(origin, blocks);
	}

	public static RadarEchoUpdateMessage delete(BlockPos origin)
	{
		return new RadarEchoUpdateMessage(origin, new FastBlockPosSet());
	}
}
