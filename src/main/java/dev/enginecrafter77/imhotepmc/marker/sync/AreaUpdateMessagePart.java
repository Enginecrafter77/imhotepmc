package dev.enginecrafter77.imhotepmc.marker.sync;

import dev.enginecrafter77.imhotepmc.marker.MarkedAreaImpl;

public class AreaUpdateMessagePart {
	public final MarkedAreaImpl group;
	public AreaUpdateEventType eventType;

	public AreaUpdateMessagePart(MarkedAreaImpl group, AreaUpdateEventType eventType)
	{
		this.group = group;
		this.eventType = eventType;
	}

	public AreaUpdateMessagePart()
	{
		this(new MarkedAreaImpl(), AreaUpdateEventType.SYNC);
	}
}
