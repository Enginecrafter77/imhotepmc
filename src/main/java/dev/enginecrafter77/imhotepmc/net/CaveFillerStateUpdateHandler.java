package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.tile.TileEntityCaveFiller;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class CaveFillerStateUpdateHandler implements IMessageHandler<CaveFillerStateUpdate, IMessage> {
	@Nullable
	@Override
	public IMessage onMessage(CaveFillerStateUpdate message, MessageContext ctx)
	{
		World world = Minecraft.getMinecraft().world;
		TileEntityCaveFiller filler = (TileEntityCaveFiller)world.getTileEntity(message.getTileEntityPosition());
		if(filler == null)
			return null;
		filler.handleClientStateUpdate(message);
		return null;
	}
}
