package dev.enginecrafter77.imhotepmc.radar;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.render.RenderCustomSizedCube;
import dev.enginecrafter77.imhotepmc.render.TesselatorRenderable;
import dev.enginecrafter77.imhotepmc.render.TextureSlice;
import dev.enginecrafter77.imhotepmc.util.FastBlockPosSet;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@SideOnly(Side.CLIENT)
public class RenderRadarOverlay {
	public static final RenderRadarOverlay INSTANCE = new RenderRadarOverlay();

	private static final TextureSlice TEXTURE = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/blocks/radar_overlay.png"), 32, 32);

	private final Map<BlockPos, RenderRadarEcho> groups;

	private RenderRadarOverlay()
	{
		this.groups = new TreeMap<BlockPos, RenderRadarEcho>();
	}

	public void setEchoGroup(BlockPos origin, FastBlockPosSet pinged)
	{
		this.groups.computeIfAbsent(origin, RenderRadarEcho::new).setPingedBlocks(pinged);
	}

	public void remove(BlockPos origin)
	{
		this.groups.remove(origin);
	}

	public void doRender(float partialTicks)
	{
		float time = Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks;
		float alpha = 0.25F + 0.125F * (float)Math.sin(time / 10F);

		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.color(1F, 1F, 1F, alpha);
		for(RenderRadarEcho group : this.groups.values())
			group.doRender(partialTicks);
		GlStateManager.disableAlpha();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	@SubscribeEvent
	public void onRenderEvent(RenderWorldLastEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != ImhotepMod.ITEM_RADAR_GLASSES)
			return;
		this.doRender(event.getPartialTicks());
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		this.groups.clear();
	}

	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(RenderRadarOverlay.INSTANCE);
	}

	public static class RenderRadarEcho extends TesselatorRenderable
	{
		private final BlockPos origin;

		private final Point3d renderPoint;
		private final Point3d originPoint;
		private final Point3d playerPos;
		private final Box3d boundingBox;

		@Nullable
		private FastBlockPosSet pingedBlocks;

		public RenderRadarEcho(BlockPos origin)
		{
			this.origin = origin;
			this.pingedBlocks = null;
			this.renderPoint = new Point3d();
			this.originPoint = new Point3d();
			this.playerPos = new Point3d();
			this.boundingBox = new Box3d();
		}

		public void setPingedBlocks(@Nullable FastBlockPosSet pingedBlocks)
		{
			this.pingedBlocks = pingedBlocks;
			this.calculateBoundingBox();
			if(pingedBlocks != null)
				this.scheduleCompilation();
		}

		private void calculateBoundingBox()
		{
			if(this.pingedBlocks == null)
			{
				this.boundingBox.set(0D, 0D, 0D, 0D, 0D, 0D);
				return;
			}

			Iterator<BlockPos> itr = this.pingedBlocks.relativeTo(this.origin).iterator();
			if(!itr.hasNext())
			{
				this.boundingBox.set(0D, 0D, 0D, 0D, 0D, 0D);
				return;
			}
			BlockPos first = itr.next();
			this.boundingBox.set(first.getX(), first.getY(), first.getZ(), first.getX() + 1D, first.getY() + 1D, first.getZ() + 1D);

			while(itr.hasNext())
			{
				BlockPos pos = itr.next();
				this.boundingBox.include(pos.getX(), pos.getY(), pos.getZ());
				this.boundingBox.include(pos.getX() + 1D, pos.getY() + 1D, pos.getZ() + 1D);
			}
		}

		private void blockRenderFaces(EnumSet<EnumFacing> out, BlockPos pos)
		{
			assert this.pingedBlocks != null;
			out.clear();
			for(EnumFacing side : EnumFacing.values())
			{
				if(!this.pingedBlocks.contains(pos.offset(side)))
					out.add(side);
			}
		}

		private boolean isPointWithinReach(Point3d point, double thresholdFactor)
		{
			assert thresholdFactor > 1F;
			double boxCrossSq = this.boundingBox.start.distanceSquared(this.boundingBox.end);
			for(Point3d corner : this.boundingBox.corners())
			{
				double distSq = corner.distanceSquared(point);
				if(distSq < (boxCrossSq * thresholdFactor))
					return true;
			}
			return false;
		}

		@Override
		public void renderIntoBuffer(BufferBuilderWrapper builder, float partialTicks)
		{
			if(this.pingedBlocks == null)
				return;

			RenderCustomSizedCube render = new RenderCustomSizedCube();
			render.setSize(1D, 1D, 1D);
			render.setAllTextureUVs(TEXTURE.asPosition());
			EnumSet<EnumFacing> renderSides = EnumSet.noneOf(EnumFacing.class);
			for(BlockPos pos : this.pingedBlocks)
			{
				this.blockRenderFaces(renderSides, pos);
				builder.setTranslation(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				render.setRenderedSides(renderSides);
				render.renderIntoBuffer(builder, 0F);
			}
		}

		@Override
		public void doRender(double x, double y, double z, float partialTicks)
		{
			TEXTURE.bind();
			super.doRender(x, y, z, partialTicks);
		}

		public void doRender(float partialTicks)
		{
			if(this.pingedBlocks == null)
				return;

			VecUtil.interpolateEntityPosition(Minecraft.getMinecraft().player, this.playerPos, partialTicks);
			if(!this.isPointWithinReach(this.playerPos, 2F))
				return;

			VecUtil.copyVec3d(this.origin, this.originPoint);
			VecUtil.calculateRenderPoint(Minecraft.getMinecraft().player, this.originPoint, this.renderPoint, partialTicks);
			this.doRender(this.renderPoint.x, this.renderPoint.y, this.renderPoint.z, partialTicks);
		}
	}
}
