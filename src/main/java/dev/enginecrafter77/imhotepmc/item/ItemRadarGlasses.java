package dev.enginecrafter77.imhotepmc.item;

import dev.enginecrafter77.imhotepmc.ImhotepConfig;
import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.render.RenderRadarEchoes;
import dev.enginecrafter77.imhotepmc.util.FastBlockPosSet;
import dev.enginecrafter77.imhotepmc.util.GraphBlockIterator;
import dev.enginecrafter77.imhotepmc.util.TickModulator;
import dev.enginecrafter77.imhotepmc.util.scheduler.CapabilityTickedTaskScheduler;
import dev.enginecrafter77.imhotepmc.util.scheduler.TickedTask;
import dev.enginecrafter77.imhotepmc.util.scheduler.TickedTaskScheduler;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;

public class ItemRadarGlasses extends Item {
	private static final Logger LOGGER = LogManager.getLogger(ItemRadarGlasses.class);

	public ItemRadarGlasses()
	{
		this.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "radar_glasses"));
		this.setTranslationKey("radar_glasses");
		this.setCreativeTab(ImhotepMod.CREATIVE_TAB);
	}

	@Nullable
	@Override
	public EntityEquipmentSlot getEquipmentSlot(ItemStack stack)
	{
		return EntityEquipmentSlot.HEAD;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		if(!event.getWorld().isRemote)
			return;
		if(event.getItemStack().getItem() != Items.STICK)
			return;

		TickedTaskScheduler scheduler = event.getWorld().getCapability(CapabilityTickedTaskScheduler.CAPABILITY, null);
		if(scheduler == null)
		{
			LOGGER.warn("Tick scheduler not available");
			return;
		}
		BlockPos start = event.getPos().offset(Objects.requireNonNull(event.getFace()));
		RenderRadarEchoes.RadarEchoGroup group = RenderRadarEchoes.INSTANCE.newGroup();
		group.setOrigin(start);
		scheduler.enqueue(new CaveScanTask(event.getWorld(), group, start));
	}

	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(ItemRadarGlasses.class);
	}

	@SideOnly(Side.CLIENT)
	private static class CaveScanTask extends TickedTask.Resultless
	{
		private final FastBlockPosSet caveModel;
		private final RenderRadarEchoes.RadarEchoGroup instance;
		private final GraphBlockIterator iterator;
		private final TickModulator tickModulator;
		private final World world;

		private final FastBlockPosSet.AbsolutePositionAdapter absCaveModel;

		public CaveScanTask(World world, RenderRadarEchoes.RadarEchoGroup instance, BlockPos origin)
		{
			this.caveModel = new FastBlockPosSet();
			this.absCaveModel = this.caveModel.relativeTo(origin);
			this.instance = instance;
			this.world = world;
			this.tickModulator = new TickModulator(this::scanOneBlock);
			this.iterator = GraphBlockIterator.bfs()
					.filter(this::canScanBlock)
					.startingAt(origin)
					.by(GraphBlockIterator.BlockExpandFunction.ALL)
					.build();
			this.tickModulator.setTickRate(64F);
		}

		private void publishCaveModel()
		{
			this.instance.setPingedBlocks(this.caveModel);
		}

		private boolean canScanBlock(BlockPos pos)
		{
			if(!this.absCaveModel.canAdd(pos))
				return false;
			return this.world.isAirBlock(pos);
		}

		private void scanOneBlock()
		{
			if(!this.iterator.hasNext() || this.caveModel.size() >= ImhotepConfig.general.caveFillerMaxBlocks)
			{
				this.publishCaveModel();
				this.complete();
				return;
			}

			BlockPos pos = this.iterator.next();
			this.absCaveModel.add(pos);
		}

		@Override
		public void update()
		{
			this.tickModulator.update();
			if(this.world.getTotalWorldTime() % 20 == 0)
				this.publishCaveModel();
		}
	}
}
