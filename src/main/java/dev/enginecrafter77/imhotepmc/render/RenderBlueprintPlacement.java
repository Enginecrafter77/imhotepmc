package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintReader;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintVoxel;
import dev.enginecrafter77.imhotepmc.blueprint.NaturalVoxelIndexer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public class RenderBlueprintPlacement implements IRenderable {
	@Nullable
	private BlueprintPlacement placement;

	@Nullable
	private BlueprintPlacementWorld placementWorld;

	@Nullable
	private CompiledVertexBuffer buffer;

	public RenderBlueprintPlacement()
	{
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
		this.buffer = null;
	}

	public void invalidate()
	{
		this.buffer = null;
	}

	protected CompiledVertexBuffer compileBuffer()
	{
		if(this.placement == null)
			throw new IllegalStateException();

		Vec3i size = this.placement.getSize();
		int volume = size.getX() * size.getY() * size.getZ();
		BufferBuilder builder = new BufferBuilder(volume * 36);

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		VertexBufferConsumer consumer = new VertexBufferConsumer(builder);

		//TODO fix blueprint reader...
		NaturalVoxelIndexer indexer = new NaturalVoxelIndexer(this.placement.getOriginOffset(), this.placement.getSize());
		for(int index = 0; index < indexer.getVolume(); ++index)
		{
			BlockPos pos = indexer.fromIndex(index);
			BlockPos offset = pos.subtract(this.placement.getOriginOffset());
			this.collectQuads(pos).forEach((BakedQuad quad) -> {
				consumer.setOffset(offset);
				quad.pipe(consumer);
			});
		}

		builder.finishDrawing();

		return CompiledVertexBuffer.compile(builder);
	}

	protected Stream<BakedQuad> collectQuads(BlockPos pos)
	{
		if(this.placementWorld == null)
			throw new IllegalStateException();

		IBlockState state = this.placementWorld.getBlockState(pos);
		EnumBlockRenderType renderType = state.getRenderType();
		if(renderType == EnumBlockRenderType.INVISIBLE)
			return Stream.of();

		IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
		Stream<BakedQuad> quadStream = model.getQuads(state, null, 0L).stream();
		for(EnumFacing face : EnumFacing.VALUES)
		{
			Stream<BakedQuad> sideQuads = model.getQuads(state, face, 0L).stream();
			quadStream = Stream.concat(quadStream, sideQuads);
		}
		return quadStream;
	}

	@Override
	public void doRender(double x, double y, double z, float partialTicks)
	{
		if(this.placement == null)
			return;
		if(this.buffer == null)
			this.buffer = this.compileBuffer();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		this.buffer.draw();
		GlStateManager.popMatrix();
	}
}
