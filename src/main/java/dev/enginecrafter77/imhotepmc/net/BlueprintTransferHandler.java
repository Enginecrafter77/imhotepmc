package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicFileFormat;
import dev.enginecrafter77.imhotepmc.net.stream.PacketStreamChunk;
import dev.enginecrafter77.imhotepmc.net.stream.server.PacketStreamServerChannel;
import dev.enginecrafter77.imhotepmc.net.stream.server.PacketStreamTopicHandler;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBlueprintLibrary;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BlueprintTransferHandler implements PacketStreamTopicHandler {
	static final Log LOGGER = LogFactory.getLog(BlueprintTransferHandler.class);

	public static final String NBT_ARG_TILEPOS = "TileEntityPosition";
	public static final String NBT_ARG_FORMAT = "Format";

	@Override
	public PacketStreamServerChannel openChannel(MessageContext ctx)
	{
		return new BlueprintTransferJob();
	}

	public void onBlueprintReceived(MessageContext ctx, BlockPos tileEntityPosition, SchematicBlueprint blueprint)
	{
		World world = ctx.getServerHandler().player.world;
		TileEntityBlueprintLibrary tile = (TileEntityBlueprintLibrary)world.getTileEntity(tileEntityPosition);
		if(tile == null)
		{
			LOGGER.error("No tile entity for Blueprint Library!");
			return;
		}

		BlueprintUploadEvent event = new BlueprintUploadEvent(tile, blueprint);
		if(MinecraftForge.EVENT_BUS.post(event))
			return;
		blueprint = event.getBlueprint();

		IItemHandlerModifiable itemHandler = (IItemHandlerModifiable)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if(itemHandler == null)
			throw new IllegalStateException();

		ItemStack stack = itemHandler.getStackInSlot(0);
		ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT.setSchematic(stack, blueprint);
		itemHandler.setStackInSlot(0, stack);
	}

	private class BlueprintTransferJob implements PacketStreamServerChannel
	{
		private final ByteBuf buffer;

		public BlueprintTransferJob()
		{
			this.buffer = Unpooled.buffer();
		}

		@Override
		public void acceptData(MessageContext ctx, PacketStreamChunk chunk)
		{
			if(!this.buffer.isWritable(chunk.getLength()))
				this.buffer.capacity(this.buffer.capacity() + chunk.getLength() * 2);
			chunk.getBuffer().readBytes(this.buffer, chunk.getLength());
		}

		@Override
		public void close(MessageContext ctx)
		{
			try
			{
				ByteBufInputStream bis = new ByteBufInputStream(this.buffer);
				NBTTagCompound tag = CompressedStreamTools.readCompressed(bis);

				SchematicFileFormat format = SchematicFileFormat.valueOf(tag.getString(NBT_ARG_FORMAT).toUpperCase());
				NBTBlueprintSerializer serializer = format.createSerializer();

				BlockPos pos = NBTUtil.getPosFromTag(tag.getCompoundTag(NBT_ARG_TILEPOS));
				SchematicBlueprint blueprint = serializer.deserializeBlueprint(tag);

				BlueprintTransferHandler.this.onBlueprintReceived(ctx, pos, blueprint);
			}
			catch(Exception exc)
			{
				LOGGER.error("Error publishing result", exc);
			}
		}
	}

	public static class BlueprintUploadEvent extends Event
	{
		private final TileEntityBlueprintLibrary library;
		private SchematicBlueprint blueprint;

		public BlueprintUploadEvent(TileEntityBlueprintLibrary library, SchematicBlueprint blueprint)
		{
			this.library = library;
			this.blueprint = blueprint;
		}

		public TileEntityBlueprintLibrary getLibrary()
		{
			return this.library;
		}

		public SchematicBlueprint getBlueprint()
		{
			return this.blueprint;
		}

		public void setBlueprint(SchematicBlueprint blueprint)
		{
			this.blueprint = blueprint;
		}

		@Override
		public boolean isCancelable()
		{
			return true;
		}
	}
}
