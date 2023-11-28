package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.tile.AreaMarkGroup;
import dev.enginecrafter77.imhotepmc.util.BlockPosEdge;
import dev.enginecrafter77.imhotepmc.util.Box3d;
import dev.enginecrafter77.imhotepmc.util.Edge3d;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.world.AreaMarkDatabase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Point3d;

@SideOnly(Side.CLIENT)
public class RenderWorldAreaMarkers {
	private static final RenderWorldAreaMarkers INSTANCE = new RenderWorldAreaMarkers();

	private final RenderTape renderTape;

	private final Point3d cameraPoint;
	private final Point3d tapeMidpoint;
	private final Point3d tapeRenderPoint;
	private final Edge3d edge;
	private final ICamera camera;

	private final Box3d groupBox;
	private final Point3d boxCenter;
	private double tapeRadius;

	public RenderWorldAreaMarkers()
	{
		this.renderTape = new RenderTape();
		this.camera = new Frustum();
		this.cameraPoint = new Point3d();
		this.tapeMidpoint = new Point3d();
		this.tapeRenderPoint = new Point3d();
		this.boxCenter = new Point3d();
		this.groupBox = new Box3d();
		this.edge = new Edge3d();
		this.tapeRadius = 0.0625D;
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{
		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewer == null)
			return;

		WorldClient world = Minecraft.getMinecraft().world;
		AreaMarkDatabase db = AreaMarkDatabase.getDefault(world);
		if(db == null)
			return;

		VecUtil.interpolateEntityPosition(viewer, this.cameraPoint, event.getPartialTicks());
		this.camera.setPosition(this.cameraPoint.x, this.cameraPoint.y, this.cameraPoint.z);

		this.renderTape.setTexture(RenderTape.TEXTURE);
		this.renderTape.setSegmentLength(1D);
		this.renderTape.setRadius(this.tapeRadius);

		for(AreaMarkGroup grp : db.getGroups())
		{
			this.groupBox.set(grp.getBoundingBox());
			this.groupBox.getCenter(this.boxCenter);
			if(this.boxCenter.distance(this.cameraPoint) > 64D)
				continue;

			for(BlockPosEdge edge : grp.getBlockEdges())
			{
				this.computeEdgeFromBPE(edge);

				this.edge.midpoint(this.tapeMidpoint);
				VecUtil.calculateRenderPoint(viewer, this.tapeMidpoint, this.tapeRenderPoint, event.getPartialTicks());
				this.renderTape.setAnchors(this.edge.getFirstPoint(), this.edge.getSecondPoint());
				this.renderTape.doRender(this.tapeRenderPoint, event.getPartialTicks());
			}
		}
	}

	public void computeEdgeFromBPE(BlockPosEdge edge)
	{
		BlockPos f = edge.getFirst();
		BlockPos s = edge.getSecond();
		this.edge.set(f.getX() + 0.5D, f.getY() + 0.5D, f.getZ() + 0.5D, s.getX() + 0.5D, s.getY() + 0.5D, s.getZ() + 0.5D);
	}

	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}
}
