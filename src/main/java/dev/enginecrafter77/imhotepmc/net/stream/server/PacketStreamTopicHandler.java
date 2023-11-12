package dev.enginecrafter77.imhotepmc.net.stream.server;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface PacketStreamTopicHandler {
	public PacketStreamServerChannel openChannel(MessageContext ctx);
}
