package dev.enginecrafter77.imhotepmc.marker.sync;

import dev.enginecrafter77.imhotepmc.marker.MarkedAreaImpl;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Objects;

public class AreaUpdateMessagePart implements IMessage {
	private final MarkedAreaImpl group;
	private AreaUpdateEventType eventType;

	public AreaUpdateMessagePart(MarkedAreaImpl group, AreaUpdateEventType eventType)
	{
		this.group = group;
		this.eventType = eventType;
	}

	public AreaUpdateMessagePart()
	{
		this(new MarkedAreaImpl(), AreaUpdateEventType.SYNC);
	}

	public MarkedAreaImpl getArea()
	{
		return this.group;
	}

	public AreaUpdateEventType getEventType()
	{
		return this.eventType;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.eventType = AreaUpdateEventType.values()[buf.readByte()];
		this.group.deserializeNBT(Objects.requireNonNull(ByteBufUtils.readTag(buf)));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(this.eventType.ordinal());
		ByteBufUtils.writeTag(buf, this.group.serializeNBT());
	}

	@Override
	public String toString()
	{
		return String.format("%s %s", this.eventType, this.group.getId());
	}
}
