package dev.enginecrafter77.imhotepmc.world.sync;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WorldDataSyncRequestHandler implements IMessageHandler<WorldDataSyncRequest, IMessage> {
	@Override
	public IMessage onMessage(WorldDataSyncRequest message, MessageContext ctx)
	{
		ImhotepMod.instance.getWorldDataSyncHandler().syncData(ctx.getServerHandler().player.world, true);
		return null;
	}
}
