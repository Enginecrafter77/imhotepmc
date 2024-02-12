package dev.enginecrafter77.imhotepmc.render;

import com.google.common.collect.Maps;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.Map;

public class BlueprintPlacementWorld implements IBlockAccess {
	private final World realWorld;
	private final BlueprintPlacement placement;

	private final BlockSelectionBox boundingBox;

	private final Map<BlockPos, TileEntity> tileCache;
	private final Map<BlockPos, IBlockState> blockStateCache;

	public BlueprintPlacementWorld(BlueprintPlacement placement, World realWorld)
	{
		this.tileCache = Maps.newHashMap();
		this.blockStateCache = Maps.newHashMap();
		this.placement = placement;
		this.realWorld = realWorld;

		this.boundingBox = new BlockSelectionBox();
		this.boundingBox.setStartSize(this.placement.getOriginOffset(), this.placement.getSize());
	}

	@Nullable
	protected TileEntity createTileForPos(BlockPos pos)
	{
		return this.placement.getBlockAt(pos).createTileEntity(this.realWorld);
	}

	@Nullable
	protected IBlockState createBlockStateFor(BlockPos pos)
	{
		IBlockState state = this.placement.getBlockAt(pos).createBlockState();
		if(state != null)
		{
			state = state.withRotation(this.placement.getRotation());
		}
		return state;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos)
	{
		if(this.boundingBox.contains(pos))
			return this.tileCache.computeIfAbsent(pos, this::createTileForPos);
		return this.realWorld.getTileEntity(pos);
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue)
	{
		return this.realWorld.getCombinedLight(pos, lightValue);
	}

	@Override
	public IBlockState getBlockState(BlockPos pos)
	{
		IBlockState state = null;
		if(this.boundingBox.contains(pos))
			state = this.blockStateCache.computeIfAbsent(pos, this::createBlockStateFor);

		if(state == null)
			state = this.realWorld.getBlockState(pos);

		return state;
	}

	@Override
	public boolean isAirBlock(BlockPos pos)
	{
		return this.getBlockState(pos).getBlock() == Blocks.AIR;
	}

	@Override
	public Biome getBiome(BlockPos pos)
	{
		return this.realWorld.getBiome(pos);
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction)
	{
		return this.realWorld.getStrongPower(pos, direction);
	}

	@Override
	public WorldType getWorldType()
	{
		return this.realWorld.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
	{
		IBlockState state = this.getBlockState(pos);
		return state.isSideSolid(this, pos, side);
	}
}
