package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.blueprint.MutableSchematicMetadata;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicMetadata;
import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nullable;
import java.time.Instant;

public class BlueprintSampleMessageHandler implements IMessageHandler<BlueprintSampleMessage, IMessage> {
	private static final Log LOGGER = LogFactory.getLog(BlueprintSampleMessageHandler.class);

	public SchematicMetadata generateMetadata(BlueprintSampleMessage message, MessageContext ctx)
	{
		Instant create = Instant.now();

		MutableSchematicMetadata metadata = new MutableSchematicMetadata();
		metadata.setName(message.getName());
		metadata.setDescription(message.getDescription());
		metadata.setCreateTime(create);
		metadata.setModifyTime(create);
		metadata.setAuthor(ctx.getServerHandler().player.getName());
		return metadata;
	}

	@Nullable
	@Override
	public IMessage onMessage(BlueprintSampleMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		TileEntity genericTile = player.world.getTileEntity(message.getArchitectTableTilePos());
		if(!(genericTile instanceof TileEntityArchitectTable))
		{
			LOGGER.error(String.format("Tile entity at %s is not TileEntityArchitectTable!", message.getArchitectTableTilePos()));
			return null;
		}
		TileEntityArchitectTable tile = (TileEntityArchitectTable)genericTile;

		SchematicMetadata metadata = this.generateMetadata(message, ctx);
		tile.scanToBlueprintItem(metadata);
		return null;
	}
}
