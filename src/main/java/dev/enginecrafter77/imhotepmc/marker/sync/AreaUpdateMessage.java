package dev.enginecrafter77.imhotepmc.marker.sync;

import dev.enginecrafter77.imhotepmc.marker.MarkedAreaImpl;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AreaUpdateMessage implements IMessage {
	private final List<AreaUpdateMessagePart> parts;
	private boolean reset;

	public AreaUpdateMessage()
	{
		this.parts = new ArrayList<>();
		this.reset = false;
	}

	public void add(MarkedAreaImpl group, AreaUpdateEventType eventType)
	{
		this.parts.add(new AreaUpdateMessagePart(group, eventType));
	}

	public void setDoReset(boolean reset)
	{
		this.reset = reset;
	}

	public boolean shouldReset()
	{
		return this.reset;
	}

	public Collection<AreaUpdateMessagePart> getParts()
	{
		return this.parts;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.reset = buf.readBoolean();
		int size = buf.readInt();
		this.parts.clear();
		for(int i = 0; i < size; ++i)
		{
			AreaUpdateMessagePart part = new AreaUpdateMessagePart();
			part.fromBytes(buf);
			this.parts.add(part);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(this.reset);
		buf.writeInt(this.parts.size());
		this.parts.forEach(p -> p.toBytes(buf));
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("AreaUpdateMessage(");
		if(this.reset)
			sb.append("RST+");
		sb.append(this.parts);
		sb.append(')');
		return sb.toString();
	}
}
