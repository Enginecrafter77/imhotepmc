package dev.enginecrafter77.imhotepmc.radar;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RadarEchoUpdateHandler implements IMessageHandler<RadarEchoUpdateMessage, IMessage> {
	@Override
	public IMessage onMessage(RadarEchoUpdateMessage message, MessageContext ctx)
	{
		if(message.getBlocks().isEmpty())
			RenderRadarOverlay.INSTANCE.remove(message.getOrigin());
		else
			RenderRadarOverlay.INSTANCE.setEchoGroup(message.getOrigin(), message.getBlocks());
		return null;
	}
}
