package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBlueprintLibrary;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageBlueprintInscribeHandler implements IMessageHandler<MessageInscribeBlueprint, IMessage> {
	private static final Log LOGGER = LogFactory.getLog(MessageBlueprintInscribeHandler.class);

	@Override
	public IMessage onMessage(MessageInscribeBlueprint message, MessageContext ctx)
	{
		if(!message.isValid())
		{
			LOGGER.error("Invalid blueprint checksum!");
			return null;
		}

		World world = ctx.getServerHandler().player.world;
		TileEntityBlueprintLibrary tile = (TileEntityBlueprintLibrary)world.getTileEntity(message.getPosition());
		if(tile == null)
		{
			LOGGER.error("No tile entity for Blueprint Library!");
			return null;
		}

		IItemHandlerModifiable itemHandler = (IItemHandlerModifiable)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if(itemHandler == null)
			throw new IllegalStateException();

		ItemStack stack = itemHandler.getStackInSlot(0);
		ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT.setSchematic(stack, message.getBlueprint());
		itemHandler.setStackInSlot(0, stack);
		return null;
	}

	public static void onBlueprintReceived(MessageContext ctx, BlockPos tileEntityPosition, SchematicBlueprint blueprint)
	{
		World world = ctx.getServerHandler().player.world;
		TileEntityBlueprintLibrary tile = (TileEntityBlueprintLibrary)world.getTileEntity(tileEntityPosition);
		if(tile == null)
		{
			LOGGER.error("No tile entity for Blueprint Library!");
			return;
		}

		IItemHandlerModifiable itemHandler = (IItemHandlerModifiable)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if(itemHandler == null)
			throw new IllegalStateException();

		ItemStack stack = itemHandler.getStackInSlot(0);
		ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT.setSchematic(stack, blueprint);
		itemHandler.setStackInSlot(0, stack);
	}
}
