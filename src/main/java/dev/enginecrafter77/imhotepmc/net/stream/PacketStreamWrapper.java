package dev.enginecrafter77.imhotepmc.net.stream;

import dev.enginecrafter77.imhotepmc.net.stream.client.PacketStreamDispatcher;
import dev.enginecrafter77.imhotepmc.net.stream.msg.*;
import dev.enginecrafter77.imhotepmc.net.stream.server.PacketStreamManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketStreamWrapper {
	private final PacketStreamManager serverSide;
	private final PacketStreamDispatcher clientSide;

	public PacketStreamWrapper(SimpleNetworkWrapper net, int bufferSize)
	{
		this.serverSide = new PacketStreamManager();
		this.clientSide = new PacketStreamDispatcher(net, bufferSize);
	}

	public PacketStreamManager getServerSide()
	{
		return this.serverSide;
	}

	public PacketStreamDispatcher getClientSide()
	{
		return this.clientSide;
	}

	public static PacketStreamWrapper create(ResourceLocation name, int bufferSize)
	{
		SimpleNetworkWrapper net = NetworkRegistry.INSTANCE.newSimpleChannel(name.toString());
		PacketStreamWrapper wrapper = new PacketStreamWrapper(net, bufferSize);

		net.registerMessage(wrapper.serverSide.getStartHandler(), PacketStreamStartMessage.class, 0, Side.SERVER);
		net.registerMessage(wrapper.serverSide.getTransferHandler(), PacketStreamTransferMessage.class, 1, Side.SERVER);
		net.registerMessage(wrapper.serverSide.getEndHandler(), PacketStreamEndMessage.class, 2, Side.SERVER);
		net.registerMessage(wrapper.clientSide.getStartConfirmHandler(), PacketStreamStartConfirmMessage.class, 3, Side.CLIENT);
		net.registerMessage(wrapper.clientSide.getTransferConfimHandler(), PacketStreamTransferConfirmMessage.class, 4, Side.CLIENT);

		return wrapper;
	}
}
