package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintReader;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintVoxel;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderBlueprintPlacement extends TesselatorRenderable {
	private final Point3d originPoint;
	private final Point3d renderPoint;

	private final List<BakedQuad> quadBuffer;

	@Nullable
	private BlueprintPlacement placement;

	@Nullable
	private BlueprintPlacementWorld placementWorld;

	public RenderBlueprintPlacement()
	{
		this.quadBuffer = new ArrayList<>(10); // 6 sides + 4 floating quads (face = null)
		this.originPoint = new Point3d();
		this.renderPoint = new Point3d();
		this.placementWorld = null;
		this.placement = null;

		this.setVertexFormat(DefaultVertexFormats.BLOCK);
	}

	public void setPlacement(@Nullable BlueprintPlacement placement)
	{
		if(placement == this.placement)
			return; // Avoid unnecessarily invalidating the buffer

		this.invalidate();
		if(placement == null)
		{
			this.placement = null;
			this.placementWorld = null;
			return;
		}

		this.placement = placement;
		this.placementWorld = new BlueprintPlacementWorld(placement, Minecraft.getMinecraft().world);
		this.originPoint.set(placement.getOriginOffset().getX(), placement.getOriginOffset().getY(), placement.getOriginOffset().getZ());
		this.compile();
	}

	@Override
	public void renderIntoBuffer(BufferBuilderWrapper bufferBuilder, float partialTicks)
	{
		if(this.placement == null || this.placementWorld == null)
			return;

		// Assemble the pipeline (RecolorVertexTransformer -> VertexLighterFlat -> VertexBufferConsumer)
		VertexBufferConsumer bufferConsumer = new VertexBufferConsumer(bufferBuilder.unwrap());
		VertexLighterFlat lighter = new VertexLighterFlat(Minecraft.getMinecraft().getBlockColors());
		lighter.setParent(bufferConsumer);
		lighter.setWorld(this.placementWorld);
		RecolorVertexTransformer alphaTransformer = new RecolorVertexTransformer(lighter);
		alphaTransformer.setTint(1F, 1F, 1F, 0.75F);

		BlueprintReader reader = this.placement.reader();
		while(reader.hasNext())
		{
			BlueprintVoxel voxel = reader.next();
			BlockPos pos = voxel.getPosition().toImmutable(); // !!IMPORTANT!!
			BlockPos offset = pos.subtract(this.placement.getOriginOffset());

			lighter.setState(this.placementWorld.getBlockState(pos));

			this.collectQuads(pos);
			for(BakedQuad quad : this.quadBuffer)
			{
				bufferConsumer.setOffset(offset);
				lighter.setBlockPos(pos);
				quad.pipe(alphaTransformer);
			}
		}
	}

	protected void collectQuads(BlockPos pos)
	{
		assert this.placementWorld != null;
		this.quadBuffer.clear();

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
			this.quadBuffer.addAll(sideQuads);
		}

		List<BakedQuad> floatingQuads = model.getQuads(state, null, 0L);
		this.quadBuffer.addAll(floatingQuads);
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		super.doRender(x, y, z, partialTicks);
	}

	public void renderInWorld(float partialTicks)
	{
		Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
		if(viewer == null)
			return;
		VecUtil.calculateRenderPoint(viewer, this.originPoint, this.renderPoint, partialTicks);
		this.doRender(this.renderPoint.x, this.renderPoint.y, this.renderPoint.z, partialTicks);
	}
}
