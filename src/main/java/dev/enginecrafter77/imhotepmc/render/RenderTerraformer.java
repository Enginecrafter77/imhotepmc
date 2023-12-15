package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.tile.TerraformMode;
import dev.enginecrafter77.imhotepmc.tile.TileEntityTerraformer;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

@SideOnly(Side.CLIENT)
public class RenderTerraformer extends TileEntitySpecialRenderer<TileEntityTerraformer> {
	private static final TextureSlice MODE_FILL = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_fill.png"), 32, 32).sub(8, 9, 16, 12);
	private static final TextureSlice MODE_CLEAR = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_clear.png"), 32, 32).sub(8, 9, 16, 12);
	private static final TextureSlice MODE_ELLIPSOID = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_ellipsoid.png"), 32, 32).sub(8, 9, 16, 12);
	private static final TextureSlice MODE_PYRAMID = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_pyramid.png"), 32, 32).sub(8, 9, 16, 12);
	private static final TextureSlice MODE_DOME = TextureSlice.full(new ResourceLocation(ImhotepMod.MOD_ID, "textures/items/shape_card_dome.png"), 32, 32).sub(8, 9, 16, 12);

	private final TextureSliceRender render;

	private final Vector3d faceOffset;
	private final Point3d faceCenterPosition;

	public RenderTerraformer()
	{
		this.faceOffset = new Vector3d();
		this.faceCenterPosition = new Point3d();
		this.render = new TextureSliceRender();
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
		case CLEAR:
			return MODE_CLEAR;
		}
	}

	@Override
	public void render(@Nonnull TileEntityTerraformer te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		EnumFacing facing = state.getValue(BlockHorizontal.FACING);
		VecUtil.copyVec3d(facing.getDirectionVec(), this.faceOffset);

		this.faceOffset.scale(0.501D);
		this.faceCenterPosition.set(x + 0.5D, y + 0.65D, z + 0.5D);
		this.faceCenterPosition.add(this.faceOffset);

		this.render.setSize(0.25D, 0.25D);
		this.render.setRotationVector(this.faceOffset);
		this.render.setTexture(this.getTextureForMode(te.getMode()));

		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

		GlStateManager.disableCull();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.COPY_INVERTED);
		this.render.doRender(this.faceCenterPosition.x, this.faceCenterPosition.y, this.faceCenterPosition.z, partialTicks);
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.COPY);
		GlStateManager.disableColorLogic();
		GlStateManager.enableCull();
	}
}
