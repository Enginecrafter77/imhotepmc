package dev.enginecrafter77.imhotepmc.marker.sync;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AreaUpdateRequest implements IMessage {
	public AreaUpdateRequest() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}
}
