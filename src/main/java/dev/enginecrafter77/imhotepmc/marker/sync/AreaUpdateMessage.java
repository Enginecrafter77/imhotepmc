package dev.enginecrafter77.imhotepmc.marker.sync;

import dev.enginecrafter77.imhotepmc.marker.MarkedAreaImpl;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
			part.eventType = AreaUpdateEventType.values()[buf.readByte()];
			part.group.deserializeNBT(Objects.requireNonNull(ByteBufUtils.readTag(buf)));
			this.parts.add(part);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(this.reset);
		buf.writeInt(this.parts.size());
		for(AreaUpdateMessagePart part : this.parts)
		{
			buf.writeByte(part.eventType.ordinal());
			ByteBufUtils.writeTag(buf, part.group.serializeNBT());
		}
	}
}
