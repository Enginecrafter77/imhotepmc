package dev.enginecrafter77.imhotepmc.cap;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.IAreaMarker;
import dev.enginecrafter77.imhotepmc.tile.TileEntityAreaMarker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Objects;

public class CapabilityAreaMarker {
	private static final ResourceLocation PLAYER_CAP_ID = new ResourceLocation(ImhotepMod.MOD_ID, "area_marker");

	@CapabilityInject(AreaMarkJob.class)
	public static Capability<AreaMarkJob> AREA_MARKER = null;

	public static void register()
	{
		CapabilityManager.INSTANCE.register(AreaMarkJob.class, AreaMarkJobStorage.INSTANCE, AreaMarkJobImpl::new);
		MinecraftForge.EVENT_BUS.register(CapabilityAreaMarker.class);
	}

	@SubscribeEvent
	public static void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof EntityPlayer)
			event.addCapability(PLAYER_CAP_ID, new AreaMarkJobHolder());
	}

	@SubscribeEvent
	public static void onInteractEvent(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getHand() != EnumHand.MAIN_HAND)
			return;

		ItemStack held = event.getItemStack();
		if(held.getItem() != ImhotepMod.ITEM_CONSTRUCTION_TAPE)
			return;

		AreaMarkJob job = event.getEntityPlayer().getCapability(AREA_MARKER, null);
		if(job == null)
			return;

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		TileEntity tile = world.getTileEntity(pos);
		boolean isMarker = tile instanceof IAreaMarker;

		BlockPos link = job.getCurrentLinkingPosition();
		if(link == null)
		{
			if(!isMarker)
				return;
			job.setCurrentLinkingPosition(pos);
		}
		else
		{
			if(!isMarker)
			{
				job.setCurrentLinkingPosition(null);
				return;
			}

			if(Objects.equals(pos, link))
			{
				job.setCurrentLinkingPosition(null);
				return;
			}

			TileEntityAreaMarker local = (TileEntityAreaMarker)tile;
			TileEntityAreaMarker other = (TileEntityAreaMarker)world.getTileEntity(link);
			if(other == null)
			{
				job.setCurrentLinkingPosition(null);
				return;
			}

			if(!world.isRemote)
				local.tryConnect(other, event.getEntityPlayer());
			job.setCurrentLinkingPosition(null);
		}
	}

	public static class AreaMarkJobStorage implements Capability.IStorage<AreaMarkJob>
	{
		private static final String NBT_KEY_POS = "linking_to";

		public static final AreaMarkJobStorage INSTANCE = new AreaMarkJobStorage();

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<AreaMarkJob> capability, AreaMarkJob instance, EnumFacing side)
		{
			NBTTagCompound tag = new NBTTagCompound();
			BlockPos link = instance.getCurrentLinkingPosition();
			if(link != null)
				tag.setTag(NBT_KEY_POS, NBTUtil.createPosTag(link));
			return tag;
		}

		@Override
		public void readNBT(Capability<AreaMarkJob> capability, AreaMarkJob instance, EnumFacing side, NBTBase nbt)
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
}
