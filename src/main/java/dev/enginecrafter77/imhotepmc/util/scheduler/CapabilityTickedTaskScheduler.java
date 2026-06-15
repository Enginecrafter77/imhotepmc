package dev.enginecrafter77.imhotepmc.util.scheduler;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class CapabilityTickedTaskScheduler {
	private static final ResourceLocation ID = new ResourceLocation(ImhotepMod.MOD_ID, "ticked_task_scheduler");

	@CapabilityInject(TickedTaskScheduler.class)
	public static Capability<TickedTaskScheduler> CAPABILITY;

	@SubscribeEvent
	public static void onWorldCapabilityAttach(AttachCapabilitiesEvent<World> event)
	{
		event.addCapability(ID, new TickedTaskSchedulerWrapper());
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if(CAPABILITY == null)
			return;
		TickedTaskSchedulerImpl scheduler = (TickedTaskSchedulerImpl) event.world.getCapability(CAPABILITY, null);
		if(scheduler == null)
			return;
		scheduler.update();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderTick(TickEvent.RenderTickEvent event)
	{
		if(CAPABILITY == null)
			return;
		@Nullable World world = Minecraft.getMinecraft().world;
		if(world == null)
			return;
		TickedTaskSchedulerImpl scheduler = (TickedTaskSchedulerImpl) world.getCapability(CAPABILITY, null);
		if(scheduler == null)
			return;
		scheduler.update();
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(TickedTaskScheduler.class, new TickedTaskSchedulerStorage(), TickedTaskSchedulerImpl::new);
		MinecraftForge.EVENT_BUS.register(CapabilityTickedTaskScheduler.class);
	}

	public static class TickedTaskSchedulerWrapper implements ICapabilityProvider
	{
		private final TickedTaskSchedulerImpl scheduler;

		public TickedTaskSchedulerWrapper()
		{
			this.scheduler = new TickedTaskSchedulerImpl();
		}

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
		{
			return capability == CAPABILITY;
		}

		@Nullable
		@Override
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
		{
			if(capability == CAPABILITY)
				return CAPABILITY.cast(this.scheduler);
			return null;
		}
	}

	public static class TickedTaskSchedulerStorage implements Capability.IStorage<TickedTaskScheduler>
	{
		@Nullable
		@Override
		public NBTBase writeNBT(Capability<TickedTaskScheduler> capability, TickedTaskScheduler instance, EnumFacing side)
		{
			return null;
		}

		@Override
		public void readNBT(Capability<TickedTaskScheduler> capability, TickedTaskScheduler instance, EnumFacing side, NBTBase nbt) {}
	}
}
