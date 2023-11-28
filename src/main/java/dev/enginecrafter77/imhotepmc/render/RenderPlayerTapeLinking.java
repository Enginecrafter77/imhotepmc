package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.cap.AreaMarkJob;
import dev.enginecrafter77.imhotepmc.cap.CapabilityAreaMarker;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

@SideOnly(Side.CLIENT)
public class RenderPlayerTapeLinking {
	private static final Vector3d HAND_POSITION = new Vector3d(0.375D, 0.75D, -0.0625D);

	private final RenderTape tapeRender;
	private final Point3d anchorPoint1;
	private final Point3d anchorPoint2;
	private final Point3d renderPoint;
	private final Point3d midPoint;

	private final Matrix3d handRotationMatrix;
	private final Vector3d handOffset;

	public RenderPlayerTapeLinking()
	{
		this.handRotationMatrix = new Matrix3d();
		this.tapeRender = new RenderTape();
		this.anchorPoint1 = new Point3d();
		this.anchorPoint2 = new Point3d();
		this.renderPoint = new Point3d();
		this.handOffset = new Vector3d();
		this.midPoint = new Point3d();
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		AreaMarkJob job = player.getCapability(CapabilityAreaMarker.AREA_MARKER, null);
		if(job == null)
			return;

		@Nullable BlockPos link = job.getCurrentLinkingPosition();
		if(link == null)
			return;

		this.anchorPoint1.set(link.getX() + 0.5D, link.getY() + 0.5D, link.getZ() + 0.5D);
		VecUtil.copyVec3d(player.getPositionVector(), this.anchorPoint2);

		this.handOffset.set(HAND_POSITION);
		this.handRotationMatrix.setIdentity();
		this.handRotationMatrix.rotY(Math.PI - Math.toRadians(player.renderArmYaw));
		this.handRotationMatrix.transform(this.handOffset);

		this.anchorPoint2.add(this.handOffset);

		this.midPoint.x = (this.anchorPoint2.x + this.anchorPoint1.x) / 2D;
		this.midPoint.y = (this.anchorPoint2.y + this.anchorPoint1.y) / 2D;
		this.midPoint.z = (this.anchorPoint2.z + this.anchorPoint1.z) / 2D;

		VecUtil.calculateRenderPoint(player, this.midPoint, this.renderPoint, event.getPartialTicks());

		this.tapeRender.setTexture(RenderTape.TEXTURE);
		this.tapeRender.setAnchors(this.anchorPoint1, this.anchorPoint2);
		this.tapeRender.doRender(this.renderPoint.x, this.renderPoint.y, this.renderPoint.z, event.getPartialTicks());
	}
}
