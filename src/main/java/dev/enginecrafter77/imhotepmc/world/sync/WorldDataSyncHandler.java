package dev.enginecrafter77.imhotepmc.world.sync;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class WorldDataSyncHandler {
	private final Set<DataSyncEntry> synchronizedNames;

	public WorldDataSyncHandler()
	{
		this.synchronizedNames = new HashSet<DataSyncEntry>();
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
			if(Objects.equals(name, ent.name))
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
		ImhotepMod.instance.getNetChannel().sendToServer(new WorldDataSyncRequest());
	}

	@SubscribeEvent
	public void onServerWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.world.isRemote)
			return;
		this.syncData(event.world, false);
	}

	public void syncData(World world, boolean force)
	{
		for(DataSyncEntry entry : this.synchronizedNames)
		{
			SynchronizedWorldSavedData sdata = entry.get(world);
			if(sdata == null)
				continue;
			if(!force && !sdata.doesNeedsSync())
				continue;

			WorldDataSyncMessage msg = new WorldDataSyncMessage(sdata);
			ImhotepMod.instance.getNetChannel().sendToAll(msg);
			sdata.setNeedsSync(false);
		}
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
