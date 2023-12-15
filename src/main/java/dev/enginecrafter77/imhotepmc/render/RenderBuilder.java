package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBuilder;
import dev.enginecrafter77.imhotepmc.util.BlockAnchor;
import dev.enginecrafter77.imhotepmc.util.BlockPosEdge;
import dev.enginecrafter77.imhotepmc.util.Edge3d;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.Rectangle;

import javax.annotation.Nonnull;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class RenderBuilder extends TileEntitySpecialRenderer<TileEntityBuilder> {
	private static final Rectangle SWITCH_RECT = new Rectangle(12, 10, 2, 3);
	private static final Dimension FACE_DIM = new Dimension(16, 16);

	private final RenderTape renderTape;

	private final Edge3d edge3d;
	private final Point3d renderPoint;
	private final Point3d midpoint;

	private final Point3d renderOrigin;

	private final RenderSwitch renderSwitch;
	private final Point3d switchRenderPoint;
	private final Point3d switchOffset;
	private final Vector2d switchSize;

	private final ItemRenderHelper itemRenderer;
	private final Vector3d faceOffset;
	private final Point3d itemDrawPos;

	public RenderBuilder()
	{
		this.renderSwitch = new RenderSwitch();
		this.switchRenderPoint = new Point3d();
		this.renderOrigin = new Point3d();
		this.switchOffset = new Point3d();
		this.switchSize = new Vector2d();
		this.itemRenderer = new ItemRenderHelper();
		this.renderTape = new RenderTape();
		this.edge3d = new Edge3d();
		this.midpoint = new Point3d();
		this.renderPoint = new Point3d();
		this.faceOffset = new Vector3d();
		this.itemDrawPos = new Point3d();

		VecUtil.spriteRect(FACE_DIM, SWITCH_RECT, this.switchOffset, this.switchSize);
		this.switchOffset.z = 0.0078125D;
	}

	private void renderMissingItem(@Nonnull TileEntityBuilder te, double x, double y, double z, float partialTicks)
	{
		Block block = te.getMissingBlock();
		if(block == null)
			return;
		ItemStack stack = ImhotepMod.instance.getBuilderBomProvider().getBlockPlaceRequiredItems(te.getWorld(), BlockPos.ORIGIN, block.getDefaultState(), null).stream().findAny().orElse(null);
		if(stack == null)
			return;

		IBlockState state = te.getWorld().getBlockState(te.getPos());
		EnumFacing facing = state.getValue(BlockHorizontal.FACING);
		VecUtil.copyVec3d(facing.getDirectionVec(), this.faceOffset);
		this.faceOffset.scale(0.5D);

		this.itemDrawPos.set(x + 0.5D, y + 0.275D, z + 0.5D);
		this.itemDrawPos.add(this.faceOffset);

		this.itemRenderer.setItem(stack);
		this.itemRenderer.setScale(0.25D);
		this.itemRenderer.setRotationByVector(this.faceOffset);
		this.itemRenderer.doRender(this.itemDrawPos, partialTicks);
	}

	@Override
	public void render(@Nonnull TileEntityBuilder te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewer == null)
			return;

		this.setLightmapDisabled(true);
		this.renderMissingItem(te, x, y, z, partialTicks);

		this.renderOrigin.set(x, y, z);
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		EnumFacing facing = state.getValue(BlockHorizontal.FACING);
		VecUtil.facePosition(this.renderOrigin, facing, this.switchOffset, this.switchRenderPoint);
		VecUtil.copyVec3d(facing.getDirectionVec(), this.faceOffset);
		this.faceOffset.negate();

		this.renderSwitch.setActive(te.isProjectionActive());
		this.renderSwitch.setSize(this.switchSize);
		this.renderSwitch.setRotationVector(this.faceOffset);
		this.renderSwitch.doRender(this.switchRenderPoint, partialTicks);

		this.renderTape.setTexture(RenderTape.TEXTURE);
		this.renderTape.setRadius(0.0625D);
		this.renderTape.setSegmentLength(1D);

		for(BlockPosEdge edge : te.getBuildAreaEdges())
		{
			this.edge3d.set(edge, BlockAnchor.CENTER, BlockAnchor.CENTER);
			this.edge3d.midpoint(this.midpoint);

			VecUtil.calculateRenderPoint(viewer, this.midpoint, this.renderPoint, partialTicks);

			this.renderTape.setAnchors(this.edge3d.getFirstPoint(), this.edge3d.getSecondPoint());
			this.renderTape.doRender(this.renderPoint, partialTicks);
		}
		this.setLightmapDisabled(false);
	}
}
