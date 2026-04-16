package dev.enginecrafter77.imhotepmc.marker;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.marker.sync.AreaUpdateEventType;
import dev.enginecrafter77.imhotepmc.marker.sync.AreaUpdateMessage;
import dev.enginecrafter77.imhotepmc.marker.sync.AreaUpdateMessagePart;
import dev.enginecrafter77.imhotepmc.marker.sync.AreaUpdateRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;

public class WorldStoredAreaMarkHandler extends AbstractAreaMarkHandler {
	private final World world;

	public WorldStoredAreaMarkHandler(World world)
	{
		super();
		this.world = world;
	}

	@Nullable
	@Override
	public MarkingAnchor getAnchorAt(BlockPos pos)
	{
		TileEntity tileEntity = this.world.getTileEntity(pos);
		if(tileEntity == null)
			return null;
		return tileEntity.getCapability(CapabilityAreaMarker.AREA_ANCHOR, null);
	}

	@Override
	protected void onAreaCreated(MarkedAreaImpl area)
	{
		/*
		 * this right here should only be done on server -- running this on both sides results
		 * in each side generating a different UUID for the zone, which results in two areas
		 * being created. Let the client skip this part and receive the update from the server.
		 */
		if(this.world.isRemote)
			return;
		super.onAreaCreated(area);
		this.broadcastAreaUpdate(area, AreaUpdateEventType.CREATE);
	}

	@Override
	protected void onAreaRemoved(MarkedAreaImpl area)
	{
		super.onAreaRemoved(area);
		this.broadcastAreaUpdate(area, AreaUpdateEventType.REMOVE);
	}

	@Override
	protected void onAreaUpdated(MarkedAreaImpl area)
	{
		super.onAreaUpdated(area);
		this.broadcastAreaUpdate(area, AreaUpdateEventType.UPDATE);
	}

	@Override
	public boolean dismantle(UUID id)
	{
		MarkedArea area = this.getArea(id);
		if(area == null)
			return false;

		AreaDismantleEvent.Pre preEvent = new AreaDismantleEvent.Pre(this.world, area);
		MinecraftForge.EVENT_BUS.post(preEvent);
		if(preEvent.isCanceled())
			return false;
		boolean dismantled = super.dismantle(id);
		MinecraftForge.EVENT_BUS.post(new AreaDismantleEvent.Post(this.world, area, dismantled));
		return dismantled;
	}

	private void broadcastAreaUpdate(MarkedAreaImpl area, AreaUpdateEventType type)
	{
		if(this.world.isRemote)
			return;
		AreaUpdateMessage msg = new AreaUpdateMessage();
		msg.add(area, type);
		ImhotepMod.instance.getNetChannel().sendToAll(msg);
	}

	@SideOnly(Side.CLIENT)
	void upsertArea(MarkedAreaImpl from)
	{
		MarkedAreaImpl existing = this.groups.get(from.getId());
		if(existing == null)
		{
			this.groups.put(from.getId(), from);
			this.writeArea(from);
			return;
		}
		this.wipeArea(existing);
		existing.set(from);
		this.writeArea(existing);
	}

	/**
	 * Writes the area ID to all the area's anchors.
	 * @param from The area to write
	 */
	@SideOnly(Side.CLIENT)
	void writeArea(MarkedAreaImpl from)
	{
		for(BlockPos pos : from.getDefiningMembers())
		{
			MarkingAnchor anchor = this.getAnchorAt(pos);
			if(anchor == null)
				continue;
			anchor.setAreaId(from.getId());
		}
	}

	/**
	 * Wipes the area ID (sets it to <code>null</code>) from all the area's anchors.
	 * @param from The area to write
	 */
	@SideOnly(Side.CLIENT)
	void wipeArea(MarkedAreaImpl from)
	{
		for(BlockPos pos : from.getDefiningMembers())
		{
			MarkingAnchor anchor = this.getAnchorAt(pos);
			if(anchor == null)
				continue;
			anchor.setAreaId(null);
		}
	}

	public static class Wrapper implements ICapabilitySerializable<NBTTagCompound>
	{
		private final WorldStoredAreaMarkHandler obj;

		public Wrapper(World world)
		{
			this.obj = new WorldStoredAreaMarkHandler(world);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
		{
			return capability == CapabilityAreaMarker.AREA_HANDLER;
		}

		@Nullable
		@Override
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
		{
			if(capability == CapabilityAreaMarker.AREA_HANDLER)
				return CapabilityAreaMarker.AREA_HANDLER.cast(this.obj);
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT()
		{
			return this.obj.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			this.obj.deserializeNBT(nbt);
		}
	}

	// server-side
	public static class AreaRequestHandler implements IMessageHandler<AreaUpdateRequest, AreaUpdateMessage>
	{
		private static final Logger LOG = LogManager.getLogger(AreaRequestHandler.class);

		@Nullable
		@Override
		public AreaUpdateMessage onMessage(AreaUpdateRequest message, MessageContext ctx)
		{
			LOG.debug("Received {}", message);
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();

			WorldStoredAreaMarkHandler handler = (WorldStoredAreaMarkHandler)world.getCapability(CapabilityAreaMarker.AREA_HANDLER, null);
			if(handler == null)
				return null;

			AreaUpdateMessage reply = new AreaUpdateMessage();
			for(MarkedAreaImpl group : handler.groups.values())
				reply.add(group, AreaUpdateEventType.SYNC);
			reply.setDoReset(true);
			return reply;
		}
	}

	// client-side
	public static class AreaUpdateHandler implements IMessageHandler<AreaUpdateMessage, IMessage>
	{
		private static final Logger LOG = LogManager.getLogger(AreaUpdateHandler.class);

		@Nullable
		@Override
		public IMessage onMessage(AreaUpdateMessage message, MessageContext ctx)
		{
			LOG.debug("Received {}", message);
			WorldStoredAreaMarkHandler handler = (WorldStoredAreaMarkHandler)Minecraft.getMinecraft().world.getCapability(CapabilityAreaMarker.AREA_HANDLER, null);
			if(handler == null)
				return null;
			if(message.shouldReset())
				handler.groups.clear();
			for(AreaUpdateMessagePart part : message.getParts())
			{
				MarkedAreaImpl area = part.getArea();
				switch(part.getEventType())
				{
				case SYNC:
					handler.upsertArea(area);
					break;
				case UPDATE:
					handler.upsertArea(area);
					handler.onAreaUpdated(area);
					break;
				case CREATE:
					handler.upsertArea(area);
					handler.onAreaCreated(area);
					break;
				case REMOVE:
					handler.wipeArea(area);
					handler.onAreaRemoved(area);
					break;
				}
			}
			return null;
		}
	}
}
