package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.tile.TileEntityBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class BuilderDwellUpdateHandler implements IMessageHandler<BuilderSharedStateUpdate, IMessage> {
	private static final Logger LOGGER = LogManager.getLogger(BuilderDwellUpdateHandler.class);

	@Nullable
	@Override
	public IMessage onMessage(BuilderSharedStateUpdate message, MessageContext ctx)
	{
		WorldClient world = Minecraft.getMinecraft().world;
		TileEntity tile = world.getTileEntity(message.getBuilderPos());
		if(!(tile instanceof TileEntityBuilder))
		{
			LOGGER.error("Tile entity for BuilderDwellUpdate is not a builder!");
			return null;
		}
		((TileEntityBuilder)tile).onStateUpdateReceived(message);
		return null;
	}
}
