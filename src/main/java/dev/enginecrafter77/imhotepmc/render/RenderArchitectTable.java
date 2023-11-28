package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import dev.enginecrafter77.imhotepmc.util.BlockPosEdge;
import dev.enginecrafter77.imhotepmc.util.Edge3d;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.vecmath.Point3d;

@SideOnly(Side.CLIENT)
public class RenderArchitectTable extends TileEntitySpecialRenderer<TileEntityArchitectTable> {
	private static final Point3d ANCHOR_MIDDLE = new Point3d(0.5D, 0.5D, 0.5D);

	private final RenderTape renderTape;

	private final Edge3d edge3d;
	private final Point3d renderPoint;
	private final Point3d midpoint;

	public RenderArchitectTable()
	{
		this.renderTape = new RenderTape();
		this.edge3d = new Edge3d();
		this.midpoint = new Point3d();
		this.renderPoint = new Point3d();
	}

	@Override
	public void render(@Nonnull TileEntityArchitectTable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewer == null)
			return;

		this.renderTape.setTexture(RenderTape.TEXTURE);
		this.renderTape.setRadius(0.0625D);
		this.renderTape.setSegmentLength(1D);

		this.setLightmapDisabled(true);
		for(BlockPosEdge edge : te.getSelectionEdges())
		{
			this.edge3d.set(edge, ANCHOR_MIDDLE);
			this.edge3d.midpoint(this.midpoint);

			VecUtil.calculateRenderPoint(viewer, this.midpoint, this.renderPoint, partialTicks);

			this.renderTape.setAnchors(this.edge3d.getFirstPoint(), this.edge3d.getSecondPoint());
			this.renderTape.doRender(this.renderPoint, partialTicks);
		}
		this.setLightmapDisabled(false);
	}
}
