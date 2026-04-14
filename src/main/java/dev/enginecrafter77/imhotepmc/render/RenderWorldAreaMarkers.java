package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.marker.AreaMarkHandler;
import dev.enginecrafter77.imhotepmc.marker.CapabilityAreaMarker;
import dev.enginecrafter77.imhotepmc.marker.MarkedArea;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Point3d;

@SideOnly(Side.CLIENT)
public class RenderWorldAreaMarkers {
	private static final RenderWorldAreaMarkers INSTANCE = new RenderWorldAreaMarkers();

	private static final Point3d PT_ZERO = new Point3d(0, 0, 0);

	private final RenderTapeArea renderTape;
	private final Box3d tapeBox;
	private final Point3d boxCenter;
	private final Point3d boxRenderPoint;

	public RenderWorldAreaMarkers()
	{
		this.renderTape = new RenderTapeArea();
		this.tapeBox = new Box3d();
		this.boxRenderPoint = new Point3d();
		this.boxCenter = new Point3d();
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{
		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewer == null)
			return;

		WorldClient world = Minecraft.getMinecraft().world;
		AreaMarkHandler handler = world.getCapability(CapabilityAreaMarker.AREA_HANDLER, null);
		if(handler == null)
			return;

		for(MarkedArea instance : handler.allAreas())
		{
			this.tapeBox.set(instance.getMarkedAreaBox());
			this.tapeBox.translate(0.5D, 0.5D, 0.5D);
			this.tapeBox.grow(-1D, -1D, -1D);
			VecUtil.boxCenter(this.tapeBox, this.boxCenter);
			VecUtil.calculateRenderPoint(viewer, this.boxCenter, this.boxRenderPoint, event.getPartialTicks());

			if(this.boxRenderPoint.distance(PT_ZERO) > 64D)
				continue;
			this.renderTape.setBox(this.tapeBox);
			this.renderTape.doRender(this.boxRenderPoint, event.getPartialTicks());
		}
	}

	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}
}
