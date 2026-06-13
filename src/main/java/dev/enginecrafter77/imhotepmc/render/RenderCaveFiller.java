package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.block.BlockCaveFiller;
import dev.enginecrafter77.imhotepmc.tile.TileEntityCaveFiller;
import dev.enginecrafter77.imhotepmc.util.math.Rect2d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderCaveFiller extends TileEntitySpecialRenderer<TileEntityCaveFiller> {
	private static final TextureSlice CONTROLS = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/misc/cave_filler_controls.png"), 64, 32);
	private static final TextureSlice[] IC_SCAN = new TextureSlice[] {
			CONTROLS.sub(0, 0, 16, 16),
			CONTROLS.sub(16, 0, 16, 16),
			CONTROLS.sub(32, 0, 16, 16),
			CONTROLS.sub(48, 0, 16, 16)
	};
	private static final TextureSlice[] IC_FILL = new TextureSlice[] {
			CONTROLS.sub(0, 16, 16, 16),
			CONTROLS.sub(16, 16, 16, 16),
			CONTROLS.sub(32, 16, 16, 16),
			CONTROLS.sub(48, 16, 16, 16)
	};
	private static final Rect2d CONTROL_DISPLAY_RECT = new Rect2d(-3D/16D, -5D/16D, 3D/16D, 0D);

	private final BlockFaceSurfaceRenderer iconRender;

	public RenderCaveFiller()
	{
		this.iconRender = new BlockFaceSurfaceRenderer();
		this.iconRender.setSurfaceRectangle(CONTROL_DISPLAY_RECT);
	}

	@Nullable
	private TextureSlice[] getActiveAnimation(TileEntityCaveFiller te)
	{
		switch(te.getState())
		{
		case SCANNING:
			return IC_SCAN;
		case FILLING:
			return IC_FILL;
		default:
			return null;
		}
	}

	@Nullable
	private TextureSlice getCurrentStateIcon(TileEntityCaveFiller te)
	{
		TextureSlice[] animation = this.getActiveAnimation(te);
		if(animation == null)
			return null;

		int animationTicks = (int)(te.getWorld().getWorldTime() % 20);
		int animationFrame = animationTicks / 5;
		return animation[animationFrame];
	}

	@Override
	public void render(TileEntityCaveFiller te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		TextureSlice icon = this.getCurrentStateIcon(te);
		if(icon != null)
		{
			this.setLightmapDisabled(true);
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			this.iconRender.setFacing(state.getValue(BlockCaveFiller.FACING));
			this.iconRender.setSprite(icon);
			this.iconRender.doRender(x, y, z, partialTicks);
			this.setLightmapDisabled(false);
		}
	}
}
