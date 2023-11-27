package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableSet;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslation;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslationContext;
import dev.enginecrafter77.imhotepmc.blueprint.translate.CommonTranslationContext;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlueprintEditor {
	private final Map<BlockPos, SavedTileState> data;

	@Nullable
	private Vec3i size;

	public BlueprintEditor()
	{
		this.data = new HashMap<BlockPos, SavedTileState>();
		this.size = null;
	}

	public void importBlueprint(Blueprint other)
	{
		other.reader().forEachRemaining(this::addVoxel);
	}

	public void addVoxel(BlueprintVoxel voxel)
	{
		this.data.put(voxel.getPosition().toImmutable(), SavedTileState.copyOf(voxel));
	}

	public BlueprintEditor setSize(Vec3i size)
	{
		this.size = size;
		return this;
	}

	public BlueprintEditor addBlock(BlockPos position, SavedBlockState data)
	{
		return this.addBlock(position, new SavedTileState(data, null));
	}

	public BlueprintEditor addBlock(BlockPos position, SavedTileState data)
	{
		this.data.put(position, data);
		return this;
	}

	public BlueprintEditor addTileEntity(BlockPos position, NBTTagCompound tileEntityData)
	{
		SavedTileState state = this.data.get(position);
		if(state == null)
			return this;
		state = state.withTileEntity(tileEntityData);
		this.data.put(position, state);
		return this;
	}

	public BlueprintEditor translate(BlueprintTranslation mapper)
	{
		BlueprintTranslationContext ctx = new CommonTranslationContext(mapper, this.data::get);

		for(BlockPos key : this.data.keySet())
		{
			SavedTileState currentState = this.data.get(key);
			SavedTileState translated = mapper.translate(ctx, key, currentState);
			if(translated == null)
				this.data.remove(key);
			else
				this.data.put(key, translated);
		}
		return this;
	}

	public RegionBlueprint build()
	{
		if(this.data.isEmpty())
			throw new UnsupportedOperationException("Cannot create empty region blueprint!");

		Vec3i size = this.size;
		Vec3i origin = Vec3i.NULL_VECTOR;

		if(size == null)
		{
			BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos();
			BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos();
			BlockPosUtil.findBoxMinMax(this.data.keySet(), min, max);
			size = max.subtract(min).add(1, 1, 1);
			origin = min.toImmutable();
		}

		SavedTileState air = SavedTileState.ofBlock(Blocks.AIR);

		VoxelIndexer indexer = new NaturalVoxelIndexer(size);
		List<SavedTileState> palette = this.data.values().stream().distinct().collect(Collectors.toList());
		palette.remove(air);
		palette.add(0, air);

		CompactPalettedBitVector<SavedTileState> vector = new CompactPalettedBitVector<SavedTileState>(palette, indexer.getVolume());
		for(Map.Entry<BlockPos, SavedTileState> entry : this.data.entrySet())
		{
			BlockPos offset = entry.getKey().subtract(origin);
			int index = indexer.toIndex(offset);
			vector.set(index, entry.getValue());
		}
		return new RegionBlueprint(indexer, ImmutableSet.copyOf(palette), vector, size, this.data.size());
	}
}
