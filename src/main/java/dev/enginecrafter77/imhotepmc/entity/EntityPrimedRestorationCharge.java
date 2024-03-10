package dev.enginecrafter77.imhotepmc.entity;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.net.DisplayRestorationParticlesMessage;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import dev.enginecrafter77.imhotepmc.util.Box3d;
import dev.enginecrafter77.imhotepmc.world.ExplosionInstance;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Point3d;
import java.util.HashMap;
import java.util.Map;

public class EntityPrimedRestorationCharge extends EntityPrimedExplosive {
	private final Map<ChunkPos, Chunk> regeneratedChunkCache;

	private double explosionRadius;

	@Nullable
	private IChunkGenerator generator;

	public EntityPrimedRestorationCharge(World worldIn)
	{
		super(worldIn);
		this.regeneratedChunkCache = new HashMap<ChunkPos, Chunk>();
		this.explosionRadius = 4D;
		this.generator = null;
		this.setFuse(80);
		this.setSize(1F, 1F);
	}

	public void setExplosionRadius(double explosionRadius)
	{
		this.explosionRadius = explosionRadius;
	}

	@Nonnull
	protected IChunkGenerator getChunkGenerator()
	{
		if(this.generator == null)
			this.generator = this.world.provider.createChunkGenerator();
		return this.generator;
	}

	protected Chunk generateChunk(ChunkPos chunkPos)
	{
		return this.getChunkGenerator().generateChunk(chunkPos.x, chunkPos.z);
	}

	@Override
	public void explode()
	{
		ExplosionInstance explosion = new ExplosionInstance(this.getPosition(), this.explosionRadius);

		for(BlockPos explodedBlock : explosion.getExplodedBlocks())
		{
			ChunkPos pos = this.world.getChunk(explodedBlock).getPos();
			Chunk regeneratedChunk = this.regeneratedChunkCache.computeIfAbsent(pos, this::generateChunk);
			IBlockState regeneratedBlockState = regeneratedChunk.getBlockState(explodedBlock);
			this.world.setBlockState(explodedBlock, regeneratedBlockState, 3);
		}

		BlockSelectionBox areaBox = explosion.getAffectedArea();
		Box3d doubleBox = new Box3d();
		doubleBox.set(areaBox);
		Point3d center = new Point3d();
		doubleBox.getCenter(center);

		NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), center.x, center.y, center.z, explosion.radius);
		DisplayRestorationParticlesMessage msg = new DisplayRestorationParticlesMessage(areaBox, 64);
		ImhotepMod.instance.getNetChannel().sendToAllAround(msg, point);
	}
}
