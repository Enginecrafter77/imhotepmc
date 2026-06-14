package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TerraformMode;
import dev.enginecrafter77.imhotepmc.tile.TileEntityTerraformer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTerraformer extends TileEntitySpecialRenderer<TileEntityTerraformer> {
	private static final TextureSlice MODE_FILL = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_fill.png"), 32, 32).sub(8, 9, 16, 12);
	private static final TextureSlice MODE_CLEAR = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_clear.png"), 32, 32).sub(8, 9, 16, 12);
	private static final TextureSlice MODE_ELLIPSOID = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_ellipsoid.png"), 32, 32).sub(8, 9, 16, 12);
	private static final TextureSlice MODE_PYRAMID = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_pyramid.png"), 32, 32).sub(8, 9, 16, 12);
	private static final TextureSlice MODE_DOME = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_dome.png"), 32, 32).sub(8, 9, 16, 12);

	private final BlockFaceSurfaceRenderer render;

	public RenderTerraformer()
	{
		this.render = new BlockFaceSurfaceRenderer();
		this.render.setSurfaceRectangle(-2/16D, -4.5/16D, 4/16D, 4/16D);
	}

	public TextureSlice getTextureForMode(TerraformMode mode)
	{
		switch(mode)
		{
		case FILL:
			return MODE_FILL;
		case ELLIPSOID:
			return MODE_ELLIPSOID;
		case PYRAMID:
			return MODE_PYRAMID;
		case DOME:
			return MODE_DOME;
		default:
			return MODE_CLEAR;
		}
	}

	@Override
	public void render(TileEntityTerraformer te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		this.render.setFacing(state.getValue(BlockHorizontal.FACING));

		this.render.setSprite(this.getTextureForMode(te.getMode()));

		this.setLightmapDisabled(true);
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.COPY_INVERTED);
		this.render.doRender(x, y, z, partialTicks);
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.COPY);
		GlStateManager.disableColorLogic();
		this.setLightmapDisabled(false);
	}
}
