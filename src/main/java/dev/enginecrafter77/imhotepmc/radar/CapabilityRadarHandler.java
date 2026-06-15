package dev.enginecrafter77.imhotepmc.radar;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityRadarHandler {
	public static final ResourceLocation ID = new ResourceLocation(ImhotepMod.MOD_ID, "radar");

	@CapabilityInject(RadarHandler.class)
	public static Capability<RadarHandler> RADAR;

	@SubscribeEvent
	public static void onCapabilityAttach(AttachCapabilitiesEvent<World> event)
	{
		event.addCapability(ID, new WorldRadarProvider(event.getObject()));
	}

	@SubscribeEvent
	public static void onServerWorldTick(TickEvent.WorldTickEvent event)
	{
		RadarTracker handler = (RadarTracker)event.world.getCapability(RADAR, null);
		if(handler == null)
			return;
		handler.update();
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(RadarHandler.class, new RadarStorage(), ShimRadarHandler::new);
		MinecraftForge.EVENT_BUS.register(CapabilityRadarHandler.class);
	}

	public static class RadarStorage implements Capability.IStorage<RadarHandler>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(Capability<RadarHandler> capability, RadarHandler instance, EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(Capability<RadarHandler> capability, RadarHandler instance, EnumFacing side, NBTBase nbt) {}
	}

	public static class WorldRadarProvider implements ICapabilityProvider
	{
		private final RadarTracker tracker;

		public WorldRadarProvider(World world)
		{
			this.tracker = new RadarTracker(world);
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
		{
			return capability == CapabilityRadarHandler.RADAR;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
		{
			if(capability == CapabilityRadarHandler.RADAR)
				return CapabilityRadarHandler.RADAR.cast(this.tracker);
			return null;
		}
	}
}
