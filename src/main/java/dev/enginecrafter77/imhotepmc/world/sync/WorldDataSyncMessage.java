package dev.enginecrafter77.imhotepmc.world.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

	public SynchronizedWorldSavedData obtainInstance(MapStorage mapStorage)
	{
		return (SynchronizedWorldSavedData)mapStorage.getOrLoadData(this.cls, this.name);
	}

	public SynchronizedWorldSavedData createNewInstance() throws ReflectiveOperationException
	{
		return this.cls.getConstructor(new Class<?>[] {String.class}).newInstance(this.name);
	}

	public NBTTagCompound getSavedData()
	{
		return this.data;
	}

	public String getName()
	{
		return this.name;
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
}
