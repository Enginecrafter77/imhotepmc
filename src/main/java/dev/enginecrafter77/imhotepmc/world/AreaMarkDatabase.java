package dev.enginecrafter77.imhotepmc.world;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.AreaMarkGroup;
import dev.enginecrafter77.imhotepmc.world.sync.SynchronizedWorldSavedData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class AreaMarkDatabase extends SynchronizedWorldSavedData {
	private final Map<UUID, AreaMarkGroup> groups;

	public AreaMarkDatabase(String name)
	{
		super(name);
		this.groups = new TreeMap<UUID, AreaMarkGroup>();
	}

	public void registerGroup(AreaMarkGroup group)
	{
		this.groups.put(group.getId(), group);
		this.markForSync();
		this.markDirty();
	}

	public void unregisterGroup(AreaMarkGroup group)
	{
		this.groups.remove(group.getId());
		this.markForSync();
		this.markDirty();
	}

	@Nullable
	public AreaMarkGroup getGroup(UUID groupId)
	{
		return this.groups.get(groupId);
	}

	public Collection<AreaMarkGroup> getGroups()
	{
		return this.groups.values();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.groups.clear();
		for(String key : nbt.getKeySet())
		{
			UUID id = UUID.fromString(key);
			AreaMarkGroup grp = new AreaMarkGroup(id);
			grp.deserializeNBT(nbt.getCompoundTag(key));
			this.groups.put(id, grp);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		for(Map.Entry<UUID, AreaMarkGroup> entry : this.groups.entrySet())
		{
			NBTTagCompound tag = entry.getValue().serializeNBT();
			compound.setTag(entry.getKey().toString(), tag);
		}
		return compound;
	}

	@Nullable
	public static AreaMarkDatabase get(World world, String name)
	{
		MapStorage map = world.getMapStorage();
		if(map == null)
			return null;
		AreaMarkDatabase db = (AreaMarkDatabase)map.getOrLoadData(AreaMarkDatabase.class, name);
		if(db == null)
		{
			db = new AreaMarkDatabase(name);
			map.setData(name, db);
		}
		return db;
	}

	@Nullable
	public static AreaMarkDatabase getDefault(World world)
	{
		return get(world, ImhotepMod.MOD_ID + ":area_markers");
	}
}
