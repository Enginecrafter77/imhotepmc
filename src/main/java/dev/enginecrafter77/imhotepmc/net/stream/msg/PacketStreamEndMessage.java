package dev.enginecrafter77.imhotepmc.net.stream.msg;

import dev.enginecrafter77.imhotepmc.net.stream.msg.PacketStreamChannelMessage;

import java.util.UUID;

public class PacketStreamEndMessage extends PacketStreamChannelMessage {
	public PacketStreamEndMessage()
	{
		super();
	}

	public PacketStreamEndMessage(UUID channelId)
	{
		super(channelId);
	}
}
