package dev.enginecrafter77.imhotepmc.world.sync;

import dev.enginecrafter77.imhotepmc.net.ReflectiveHandlerWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class WorldDataSyncHandler {
	private final Set<DataSyncEntry> synchronizedNames;

	private final SimpleNetworkWrapper network;

	public WorldDataSyncHandler(SimpleNetworkWrapper network)
	{
		this.synchronizedNames = new HashSet<DataSyncEntry>();
		this.network = network;
	}

	public void register(Class<? extends SynchronizedWorldSavedData> cls, String name)
	{
		this.synchronizedNames.add(new DataSyncEntry(cls, name));
	}

	public void unregister(String name)
	{
		Iterator<DataSyncEntry> itr = this.synchronizedNames.iterator();
		while(itr.hasNext())
		{
			DataSyncEntry ent = itr.next();
			if(Objects.equals(name, ent.getName()))
			{
				itr.remove();
				return;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientWorldLoadedEvent(EntityJoinWorldEvent event)
	{
		this.network.sendToServer(new WorldDataSyncRequest());
	}

	@SubscribeEvent
	public void onServerWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.world.isRemote)
			return;
		WorldDataSyncBundleMessage bundle = this.getDataSyncMessage(event.world, false);
		if(bundle != null)
			this.network.sendToAll(bundle);
	}

	@Nullable
	public WorldDataSyncBundleMessage getDataSyncMessage(World world, boolean force)
	{
		WorldDataSyncBundleMessage bundle = null;
		for(DataSyncEntry entry : this.synchronizedNames)
		{
			SynchronizedWorldSavedData sdata = entry.get(world);
			if(sdata == null)
				continue;
			if(!force && !sdata.doesNeedsSync())
				continue;

			if(bundle == null)
				bundle = new WorldDataSyncBundleMessage();

			bundle.add(sdata);
			sdata.setNeedsSync(false);
		}
		return bundle;
	}

	public IMessage onDataSyncRequest(WorldDataSyncRequest request, MessageContext ctx)
	{
		return this.getDataSyncMessage(ctx.getServerHandler().player.world, true);
	}

	@SideOnly(Side.CLIENT)
	public IMessage onDataSyncReceived(WorldDataSyncMessage message, MessageContext ctx)
	{
		World world = Minecraft.getMinecraft().world;
		MapStorage ms = world.getMapStorage();
		if(ms == null)
			return null;

		SynchronizedWorldSavedData sd = message.obtainInstance(ms);
		if(sd == null)
		{
			try
			{
				sd = message.createNewInstance();
				ms.setData(message.getName(), sd);
			}
			catch(ReflectiveOperationException exc)
			{
				throw new RuntimeException(exc);
			}
		}
		sd.deserializeNBT(message.getSavedData());
		return null;
	}

	@SideOnly(Side.CLIENT)
	public IMessage onDataSyncBundleReceived(WorldDataSyncBundleMessage message, MessageContext ctx)
	{
		for(WorldDataSyncMessage msg : message.getParts())
			this.onDataSyncReceived(msg, ctx);
		return null;
	}

	public static WorldDataSyncHandler create(ResourceLocation name)
	{
		SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(name.toString());
		WorldDataSyncHandler handler = new WorldDataSyncHandler(network);

		network.registerMessage(ReflectiveHandlerWrapper.create(WorldDataSyncHandler.class, WorldDataSyncMessage.class, handler, "onDataSyncReceived"), WorldDataSyncMessage.class, 0, Side.CLIENT);
		network.registerMessage(ReflectiveHandlerWrapper.create(WorldDataSyncHandler.class, WorldDataSyncBundleMessage.class, handler, "onDataSyncBundleReceived"), WorldDataSyncBundleMessage.class, 1, Side.CLIENT);
		network.registerMessage(ReflectiveHandlerWrapper.create(WorldDataSyncHandler.class, WorldDataSyncRequest.class, handler, "onDataSyncRequest"), WorldDataSyncRequest.class, 2, Side.SERVER);

		return handler;
	}

	private static class DataSyncEntry
	{
		private final Class<? extends SynchronizedWorldSavedData> cls;
		private final String name;

		public DataSyncEntry(Class<? extends SynchronizedWorldSavedData> cls, String name)
		{
			this.cls = cls;
			this.name = name;
		}

		public String getName()
		{
			return this.name;
		}

		@Nullable
		public SynchronizedWorldSavedData get(World world)
		{
			MapStorage map = world.getMapStorage();
			if(map == null)
				return null;
			return (SynchronizedWorldSavedData)map.getOrLoadData(this.cls, this.name);
		}
	}
}
