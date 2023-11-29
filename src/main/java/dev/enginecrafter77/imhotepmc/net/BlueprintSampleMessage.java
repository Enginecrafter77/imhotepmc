package dev.enginecrafter77.imhotepmc.net;

import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

public class BlueprintSampleMessage implements IMessage {
	@Nullable
	private String name;
	@Nullable
	private String description;
	@Nullable
	private BlockPos architectTableTilePos;

	public BlueprintSampleMessage()
	{
		this.architectTableTilePos = null;
		this.name = null;
		this.description = null;
	}

	public BlueprintSampleMessage(BlockPos architectTableTilePos, String name, String description)
	{
		this.architectTableTilePos = architectTableTilePos;
		this.description = description;
		this.name = name;
	}

	public BlockPos getArchitectTableTilePos()
	{
		if(this.architectTableTilePos == null)
			throw new IllegalStateException();
		return this.architectTableTilePos;
	}

	public String getName()
	{
		if(this.name == null)
			throw new IllegalStateException();
		return this.name;
	}

	public String getDescription()
	{
		if(this.description == null)
			throw new IllegalStateException();
		return this.description;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.architectTableTilePos = BlockPosUtil.readFromByteBuf(buf);
		this.name = ByteBufUtils.readUTF8String(buf);
		this.description = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		if(this.name == null || this.description == null || this.architectTableTilePos == null)
			throw new IllegalStateException();
		BlockPosUtil.writeToByteBuf(buf, this.architectTableTilePos);
		ByteBufUtils.writeUTF8String(buf, this.name);
		ByteBufUtils.writeUTF8String(buf, this.description);
	}
}
