package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class DisplayRestorationParticlesMessage implements IMessage {
	private final BlockSelectionBox box;
	private int count;

	public DisplayRestorationParticlesMessage(BlockSelectionBox box, int count)
	{
		this.box = box;
		this.count = count;
	}

	public DisplayRestorationParticlesMessage()
	{
		this.box = new BlockSelectionBox();
		this.count = 0;
	}

	public BlockSelectionBox getBox()
	{
		return this.box;
	}

	public int getCount()
	{
		return this.count;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int mx = buf.readInt();
		int my = buf.readInt();
		int mz = buf.readInt();
		int Mx = buf.readInt();
		int My = buf.readInt();
		int Mz = buf.readInt();
		this.count = buf.readShort();
		this.box.setStartEnd(new BlockPos(mx, my, mz), new BlockPos(Mx, My, Mz));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		BlockPos min = this.box.getMinCorner();
		BlockPos max = this.box.getMaxCorner();
		buf.writeInt(min.getX());
		buf.writeInt(min.getY());
		buf.writeInt(min.getZ());
		buf.writeInt(max.getX());
		buf.writeInt(max.getY());
		buf.writeInt(max.getZ());
		buf.writeShort(this.count);
	}
}
