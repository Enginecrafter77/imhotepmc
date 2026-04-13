package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3d;
import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Point3d;

@SideOnly(Side.CLIENT)
public class RenderArchitectTable extends TileEntitySpecialRenderer<TileEntityArchitectTable> {
	private final RenderTapeArea renderTapeArea;

	private final Point3d boxRenderPoint;
	private final Point3d boxCenter;
	private final Box3d box;

	public RenderArchitectTable()
	{
		this.renderTapeArea = new RenderTapeArea();
		this.boxRenderPoint = new Point3d();
		this.boxCenter = new Point3d();
		this.box = new Box3d();
	}

	@Override
	public void render(TileEntityArchitectTable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewer == null)
			return;

		Box3i s = te.getSelection();
		this.box.set(s.start.x + 0.5D, s.start.y + 0.5D, s.start.z + 0.5D, s.end.x - 0.5D, s.end.y - 0.5D, s.end.z - 0.5D);
		VecUtil.boxCenter(this.box, this.boxCenter);
		VecUtil.calculateRenderPoint(viewer, this.boxCenter, this.boxRenderPoint, partialTicks);

		this.setLightmapDisabled(true);
		this.renderTapeArea.doRender(this.boxRenderPoint, partialTicks);
		this.setLightmapDisabled(false);
	}
}
