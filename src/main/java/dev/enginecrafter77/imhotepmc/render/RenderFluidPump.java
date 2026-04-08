package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.tile.TileEntityFluidPump;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFluidPump extends TileEntitySpecialRenderer<TileEntityFluidPump> {
	private final RenderFluidPumpPipe pipeRender;

	public RenderFluidPump()
	{
		this.pipeRender = new RenderFluidPumpPipe();
		this.pipeRender.setSegmentLength(1D);
	}

	@Override
	public void render(TileEntityFluidPump te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		int segments = (int)Math.ceil(te.getPipeExtension());
		double pipeSegmentRetraction = segments - te.getPipeExtension();
		this.pipeRender.setSegmentCount(segments);

		this.setLightmapDisabled(true);
		this.pipeRender.doRender(x + 0.5D, y + pipeSegmentRetraction, z + 0.5D, partialTicks);
		this.setLightmapDisabled(false);
	}
}
