package dev.enginecrafter77.imhotepmc.world.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldDataSyncMessage implements IMessage {
	private Class<? extends SynchronizedWorldSavedData> cls;
	private NBTTagCompound data;
	private String name;

	public WorldDataSyncMessage(SynchronizedWorldSavedData wsd)
	{
		this.cls = wsd.getClass();
		this.data = wsd.serializeNBT();
		this.name = wsd.mapName;
	}

	public WorldDataSyncMessage()
	{
		this.cls = null;
		this.data = null;
		this.name = null;
	}

	@SideOnly(Side.CLIENT)
	public void apply(World world)
	{
		MapStorage ms = world.getMapStorage();
		if(ms == null)
			return;
		SynchronizedWorldSavedData sd = (SynchronizedWorldSavedData)ms.getOrLoadData(this.cls, this.name);
		if(sd == null)
		{
			try
			{
				sd = this.cls.getConstructor(new Class<?>[] {String.class}).newInstance(this.name);
				ms.setData(this.name, sd);
			}
			catch(ReflectiveOperationException exc)
			{
				throw new RuntimeException(exc);
			}
		}
		sd.deserializeNBT(this.data);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		String clsname = ByteBufUtils.readUTF8String(buf);
		try
		{
			this.cls = this.getClass().getClassLoader().loadClass(clsname).asSubclass(SynchronizedWorldSavedData.class);
			this.name = ByteBufUtils.readUTF8String(buf);
			this.data = ByteBufUtils.readTag(buf);
		}
		catch(Exception exc)
		{
			throw new RuntimeException(exc);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, this.cls.getName());
		ByteBufUtils.writeUTF8String(buf, this.name);
		ByteBufUtils.writeTag(buf, this.data);
	}

	public static class WorldDataSyncMessageHandler implements IMessageHandler<WorldDataSyncMessage, IMessage>
	{
		@Override
		public IMessage onMessage(WorldDataSyncMessage message, MessageContext ctx)
		{
			message.apply(Minecraft.getMinecraft().world);
			return null;
		}
	}
}
