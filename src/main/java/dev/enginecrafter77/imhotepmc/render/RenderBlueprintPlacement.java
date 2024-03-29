package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintReader;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintVoxel;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderBlueprintPlacement implements IAutoRenderable {
	private final Point3d originPoint;
	private final Point3d renderPoint;

	@Nullable
	private BlueprintPlacement placement;

	@Nullable
	private BlueprintPlacementWorld placementWorld;

	@Nullable
	private CompiledVertexBuffer buffer;

	public RenderBlueprintPlacement()
	{
		this.originPoint = new Point3d();
		this.renderPoint = new Point3d();
		this.placementWorld = null;
		this.placement = null;
		this.buffer = null;
	}

	public void setPlacement(@Nullable BlueprintPlacement placement)
	{
		if(placement == this.placement)
			return; // Avoid unnecessarily invalidating the buffer

		if(placement == null)
		{
			this.placement = null;
			this.placementWorld = null;
			this.buffer = null;
			return;
		}

		this.placement = placement;
		this.placementWorld = new BlueprintPlacementWorld(placement, Minecraft.getMinecraft().world);
		VecUtil.copyVec3d(placement.getOriginOffset(), this.originPoint);
		this.buffer = null;
	}

	public void invalidate()
	{
		this.buffer = null;
	}

	protected CompiledVertexBuffer compileBuffer()
	{
		if(this.placement == null || this.placementWorld == null)
			throw new IllegalStateException();

		Vec3i size = this.placement.getSize();
		int volume = size.getX() * size.getY() * size.getZ();
		BufferBuilder builder = new BufferBuilder(volume * 36);
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		// Assemble the pipeline (RecolorVertexTransformer -> VertexLighterFlat -> VertexBufferConsumer)
		VertexBufferConsumer bufferConsumer = new VertexBufferConsumer(builder);
		VertexLighterFlat lighter = new VertexLighterFlat(Minecraft.getMinecraft().getBlockColors());
		lighter.setParent(bufferConsumer);
		lighter.setWorld(this.placementWorld);
		RecolorVertexTransformer alphaTransformer = new RecolorVertexTransformer(lighter);
		alphaTransformer.setTint(1F, 1F, 1F, 0.75F);

		List<BakedQuad> quads = new ArrayList<BakedQuad>(10); // 6 sides + 4 floating quads (face = null)

		BlueprintReader reader = this.placement.reader();
		while(reader.hasNext())
		{
			BlueprintVoxel voxel = reader.next();
			BlockPos pos = voxel.getPosition().toImmutable(); // !!IMPORTANT!!
			BlockPos offset = pos.subtract(this.placement.getOriginOffset());

			lighter.setState(this.placementWorld.getBlockState(pos));

			quads.clear();
			this.collectQuads(pos, quads);

			for(BakedQuad quad : quads)
			{
				bufferConsumer.setOffset(offset);
				lighter.setBlockPos(pos);
				quad.pipe(alphaTransformer);
			}
		}

		builder.finishDrawing();

		return CompiledVertexBuffer.compile(builder);
	}

	protected void collectQuads(BlockPos pos, List<BakedQuad> dest)
	{
		if(this.placementWorld == null)
			throw new IllegalStateException();

		// Electric Boogaloo
		IBlockState state = this.placementWorld.getBlockState(pos);
		state = state.getActualState(this.placementWorld, pos);
		state = state.getBlock().getExtendedState(state, this.placementWorld, pos);

		EnumBlockRenderType renderType = state.getRenderType();
		if(renderType == EnumBlockRenderType.INVISIBLE)
			return;

		IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
		for(EnumFacing face : EnumFacing.VALUES)
		{
			if(!state.shouldSideBeRendered(this.placementWorld, pos, face))
				continue;
			List<BakedQuad> sideQuads = model.getQuads(state, face, 0L);
			dest.addAll(sideQuads);
		}

		List<BakedQuad> floatingQuads = model.getQuads(state, null, 0L);
		dest.addAll(floatingQuads);
	}

	@Override
	public void doRender(float partialTicks)
	{
		if(this.placement == null)
			return;

		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewer == null)
			return;
		VecUtil.calculateRenderPoint(viewer, this.originPoint, this.renderPoint, partialTicks);

		if(this.buffer == null)
			this.buffer = this.compileBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.translate(this.renderPoint.x, this.renderPoint.y, this.renderPoint.z);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		this.buffer.draw();
		GlStateManager.popMatrix();
	}
}
