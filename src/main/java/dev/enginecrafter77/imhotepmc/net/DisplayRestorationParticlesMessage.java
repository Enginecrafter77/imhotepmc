package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class DisplayRestorationParticlesMessage implements IMessage {
	private final Box3i box;
	private int count;

	public DisplayRestorationParticlesMessage(Box3i box, int count)
	{
		this.box = box;
		this.count = count;
	}

	public DisplayRestorationParticlesMessage()
	{
		this.box = new Box3i();
		this.count = 0;
	}

	public Box3i getBox()
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
		this.box.start.x = buf.readInt();
		this.box.start.y = buf.readInt();
		this.box.start.z = buf.readInt();
		this.box.end.x = buf.readInt();
		this.box.end.y = buf.readInt();
		this.box.end.z = buf.readInt();
		this.count = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.box.start.x);
		buf.writeInt(this.box.start.y);
		buf.writeInt(this.box.start.z);
		buf.writeInt(this.box.end.x);
		buf.writeInt(this.box.end.y);
		buf.writeInt(this.box.end.z);
		buf.writeShort(this.count);
	}
}
