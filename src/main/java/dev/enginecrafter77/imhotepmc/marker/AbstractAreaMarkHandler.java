package dev.enginecrafter77.imhotepmc.marker;

import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

public abstract class AbstractAreaMarkHandler implements AreaMarkHandler {
	protected final Map<UUID, MarkedAreaImpl> groups;

	public AbstractAreaMarkHandler()
	{
		this.groups = new HashMap<>();
	}

	protected void onAreaCreated(MarkedAreaImpl area) {}
	protected void onAreaRemoved(MarkedAreaImpl area) {}
	protected void onAreaUpdated(MarkedAreaImpl area) {}

	private void registerArea(MarkedAreaImpl group)
	{
		this.groups.put(group.getId(), group);
		this.onAreaCreated(group);
	}

	private void unregisterArea(MarkedAreaImpl group)
	{
		this.groups.remove(group.getId());
		this.onAreaRemoved(group);
	}

	@Nullable
	@Override
	public MarkedArea getArea(UUID id)
	{
		return this.groups.get(id);
	}

	@Override
	public Collection<? extends MarkedArea> allAreas()
	{
		return this.groups.values();
	}

	@Nullable
	private MarkedAreaImpl tryGetInstance(MarkingAnchor marker)
	{
		@Nullable UUID id = marker.getAreaId();
		if(id == null)
			return null;
		return this.groups.get(id);
	}

	@Override
	public AreaExpandResult connect(AreaMarkingActor actor, MarkingAnchor first, MarkingAnchor second, boolean simulate)
	{
		if(first == second)
			throw new IllegalArgumentException("Cannot connect area marker to itself");

		MarkedAreaImpl i1 = this.tryGetInstance(first);
		MarkedAreaImpl i2 = this.tryGetInstance(second);
		int mask = 0;
		if(i1 != null)
			mask |= 0x1;
		if(i2 != null)
			mask |= 0x2;

		AreaExpandResult result;
		switch(mask)
		{
		case 0: // both are null
			if(BlockPosUtil.getSharedAxis(first.getMarkerPosition(), second.getMarkerPosition()) == null)
				return AreaExpandResult.NO_CONNECTING_AXIS;
			if(!simulate)
			{
				MarkedAreaImpl group = MarkedAreaImpl.create(first.getMarkerPosition(), second.getMarkerPosition());
				first.setAreaId(group.getId());
				second.setAreaId(group.getId());
				this.registerArea(group);
			}
			return AreaExpandResult.SUCCESS;
		case 1: // i1 != null && i2 == null
			result = i1.expand(second.getMarkerPosition(), simulate);
			if(!simulate && result == AreaExpandResult.SUCCESS)
			{
				second.setAreaId(i1.getId());
				this.onAreaUpdated(i1);
			}
			return result;
		case 2: // i1 == null && i2 != null
			return connect(actor, second, first, simulate); // flip the arguments
		default:
			return i1 == i2 ? AreaExpandResult.ALREADY_ADDED : AreaExpandResult.CONFLICT;
		}
	}

	@Override
	public void disconnect(MarkingAnchor first)
	{
		UUID groupId = first.getAreaId();
		if(groupId == null)
			return;
		MarkedAreaImpl group = this.groups.get(groupId);
		if(group == null)
		{
			//TODO warning
			first.setAreaId(null);
			return;
		}
		group.remove(first.getMarkerPosition());

		if(group.isEmpty())
			this.unregisterArea(group);
	}

	@Override
	public boolean dismantle(UUID id)
	{
		MarkedAreaImpl group = this.groups.get(id);
		if(group == null)
			return false;

		// Pass 1: unlink
		for(BlockPos pos : group.getDefiningMembers())
		{
			MarkingAnchor marker = this.getAnchorAt(pos);
			if(marker == null)
				continue; //TODO warning
			marker.setAreaId(null);
		}

		// Pass 2: dismantle
		for(BlockPos pos : group.getDefiningMembers())
		{
			MarkingAnchor marker = this.getAnchorAt(pos);
			if(marker == null)
				continue; //TODO warning
			marker.dismantle();
		}
		this.unregisterArea(group);
		return true;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.groups.clear();
		NBTTagList list = nbt.getTagList("areas", 10);
		for(NBTBase baseTag : list)
		{
			NBTTagCompound areaTag = (NBTTagCompound)baseTag;
			MarkedAreaImpl grp = new MarkedAreaImpl();
			grp.deserializeNBT(areaTag);
			this.groups.put(grp.getId(), grp);
		}
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagList list = new NBTTagList();
		for(MarkedAreaImpl entry : this.groups.values())
			list.appendTag(entry.serializeNBT());

		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("areas", list);
		return compound;
	}
}
