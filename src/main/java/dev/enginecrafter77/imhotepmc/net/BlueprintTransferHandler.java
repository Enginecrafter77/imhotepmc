package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.blueprint.NBTBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.net.stream.PacketStreamChunk;
import dev.enginecrafter77.imhotepmc.net.stream.server.PacketStreamTopicHandler;
import dev.enginecrafter77.imhotepmc.net.stream.server.PacketStreamServerChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class BlueprintTransferHandler implements PacketStreamTopicHandler {
	private static final Log LOGGER = LogFactory.getLog(BlueprintTransferHandler.class);

	private final BlueprintTransferResultConsumer resultConsumer;
	private final NBTBlueprintSerializer serializer;

	public BlueprintTransferHandler(NBTBlueprintSerializer serializer, BlueprintTransferResultConsumer resultConsumer)
	{
		this.resultConsumer = resultConsumer;
		this.serializer = serializer;
	}

	@Override
	public PacketStreamServerChannel openChannel(MessageContext ctx)
	{
		return new BlueprintTransferJob();
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

				BlockPos pos = NBTUtil.getPosFromTag(tag.getCompoundTag("TileEntityPosition"));
				SchematicBlueprint blueprint = BlueprintTransferHandler.this.serializer.deserializeBlueprint(tag);

				BlueprintTransferHandler.this.resultConsumer.onResultReceived(ctx, pos, blueprint);
			}
			catch(IOException exc)
			{
				LOGGER.error("Error publishing result", exc);
			}
		}
	}

	public static interface BlueprintTransferResultConsumer
	{
		public void onResultReceived(MessageContext ctx, BlockPos tileEntityPosition, SchematicBlueprint blueprint);
	}
}
