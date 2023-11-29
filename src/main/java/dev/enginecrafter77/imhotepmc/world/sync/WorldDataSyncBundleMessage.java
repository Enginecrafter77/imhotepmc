package dev.enginecrafter77.imhotepmc.world.sync;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorldDataSyncBundleMessage implements IMessage {
	private final List<WorldDataSyncMessage> parts;

	public WorldDataSyncBundleMessage()
	{
		this.parts = new ArrayList<WorldDataSyncMessage>();
	}

	public void add(SynchronizedWorldSavedData data)
	{
		this.parts.add(new WorldDataSyncMessage(data));
	}

	public Collection<WorldDataSyncMessage> getParts()
	{
		return this.parts;
	}

	public boolean isEmpty()
	{
		return this.parts.isEmpty();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.parts.clear();

		int count = buf.readShort();
		for(int index = 0; index < count; ++index)
		{
			WorldDataSyncMessage msg = new WorldDataSyncMessage();
			msg.fromBytes(buf);
			this.parts.add(msg);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeShort(this.parts.size());
		for(WorldDataSyncMessage msg : this.parts)
			msg.toBytes(buf);
	}
}
