package dev.enginecrafter77.imhotepmc.net.stream.server;

import dev.enginecrafter77.imhotepmc.net.stream.PacketStreamChunk;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface PacketStreamServerChannel {
	public void acceptData(MessageContext ctx, PacketStreamChunk chunk);
	public void close(MessageContext ctx);
}
