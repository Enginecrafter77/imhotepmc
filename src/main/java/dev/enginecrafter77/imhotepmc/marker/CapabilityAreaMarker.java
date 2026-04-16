package dev.enginecrafter77.imhotepmc.marker;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.marker.sync.AreaUpdateRequest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class CapabilityAreaMarker {
	private static final ResourceLocation PLAYER_CAP_ID = new ResourceLocation(ImhotepMod.MOD_ID, "area_marker");
	private static final ResourceLocation WORLD_CAP_ID = new ResourceLocation(ImhotepMod.MOD_ID, "area_marker");

	@CapabilityInject(MarkingAnchor.class)
	public static Capability<MarkingAnchor> AREA_ANCHOR;

	@CapabilityInject(AreaMarkingActor.class)
	public static Capability<AreaMarkingActor> AREA_MARKING_ACTOR;

	@CapabilityInject(AreaMarkHandler.class)
	public static Capability<AreaMarkHandler> AREA_HANDLER;

	@SubscribeEvent
	public static void onPlayerCapabilityAttach(AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof EntityPlayer)
			event.addCapability(PLAYER_CAP_ID, new AreaMarkingActorWrapper());
	}

	@SubscribeEvent
	public static void onWorldCapabilityAttach(AttachCapabilitiesEvent<World> event)
	{
		event.addCapability(WORLD_CAP_ID, new WorldStoredAreaMarkHandler.Wrapper(event.getObject()));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientWorldLoadedEvent(EntityJoinWorldEvent event)
	{
		ImhotepMod.instance.getNetChannel().sendToServer(new AreaUpdateRequest());
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(MarkingAnchor.class, AreaMarkerStorage.INSTANCE, MarkingAnchorImpl::new);
		CapabilityManager.INSTANCE.register(AreaMarkingActor.class, AreaMarkingActorStorage.INSTANCE, AreaMarkingActorImpl::new);
		CapabilityManager.INSTANCE.register(AreaMarkHandler.class, AreaMarkHandlerStorage.INSTANCE, DummyAreaMarkHandler::new);
		MinecraftForge.EVENT_BUS.register(CapabilityAreaMarker.class);
	}

	public static class AreaMarkerStorage implements Capability.IStorage<MarkingAnchor> {
		private static final String NBT_KEY_GROUP_PRESENT = "group_present";
		private static final String NBT_KEY_GROUP = "group";

		public static final AreaMarkerStorage INSTANCE = new AreaMarkerStorage();

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<MarkingAnchor> capability, MarkingAnchor instance, EnumFacing side)
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(NBT_KEY_GROUP_PRESENT, instance.getAreaId() != null);
			if(instance.getAreaId() != null)
				tag.setUniqueId(NBT_KEY_GROUP, instance.getAreaId());
			return tag;
		}

		@Override
		public void readNBT(Capability<MarkingAnchor> capability, MarkingAnchor instance, EnumFacing side, NBTBase nbt)
		{
			NBTTagCompound tag = (NBTTagCompound)nbt;
			boolean present = tag.getBoolean(NBT_KEY_GROUP_PRESENT);
			if(present)
				instance.setAreaId(tag.getUniqueId(NBT_KEY_GROUP));
		}
	}

	public static class AreaMarkingActorStorage implements Capability.IStorage<AreaMarkingActor>
	{
		private static final String NBT_KEY_POS = "linking_to";

		public static final AreaMarkingActorStorage INSTANCE = new AreaMarkingActorStorage();

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<AreaMarkingActor> capability, AreaMarkingActor instance, EnumFacing side)
		{
			NBTTagCompound tag = new NBTTagCompound();
			BlockPos link = instance.getCurrentLinkingPosition();
			if(link != null)
				tag.setTag(NBT_KEY_POS, NBTUtil.createPosTag(link));
			return tag;
		}

		@Override
		public void readNBT(Capability<AreaMarkingActor> capability, AreaMarkingActor instance, EnumFacing side, NBTBase nbt)
		{
			NBTTagCompound tag = (NBTTagCompound)nbt;
			if(!tag.hasKey(NBT_KEY_POS))
			{
				instance.setCurrentLinkingPosition(null);
				return;
			}
			BlockPos pos = NBTUtil.getPosFromTag(tag.getCompoundTag(NBT_KEY_POS));
			instance.setCurrentLinkingPosition(pos);
		}
	}

	public static class AreaMarkHandlerStorage implements Capability.IStorage<AreaMarkHandler> {
		public static final AreaMarkHandlerStorage INSTANCE = new AreaMarkHandlerStorage();

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<AreaMarkHandler> capability, AreaMarkHandler instance, EnumFacing side)
		{
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<AreaMarkHandler> capability, AreaMarkHandler instance, EnumFacing side, NBTBase nbt)
		{
			instance.deserializeNBT((NBTTagCompound)nbt);
		}
	}
}
