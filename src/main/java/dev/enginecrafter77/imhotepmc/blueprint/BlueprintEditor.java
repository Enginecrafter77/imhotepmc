package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableSet;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslation;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslationContext;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
		this.addBlock(voxel.getPosition().toImmutable(), voxel);
	}

	public BlueprintEditor setSize(Vec3i size)
	{
		this.size = size;
		return this;
	}

	public BlueprintEditor addBlock(BlockPos position, BlueprintEntry block)
	{
		if(Objects.equals(block.getBlockName(), Blocks.AIR.getRegistryName()))
			return this;
		this.data.put(position, SavedTileState.copyOf(block));
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
		BlueprintTranslationContext ctx = BlueprintTranslationContext.dummy();

		for(BlockPos key : this.data.keySet())
		{
			SavedTileState currentState = this.data.get(key);
			BlueprintEntry translated = mapper.translate(ctx, key, currentState);
			if(translated == null)
				this.data.remove(key);
			else
				this.data.put(key, SavedTileState.copyOf(translated));
		}
		return this;
	}

	public StructureBlueprint build()
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

		VoxelIndexer indexer = new NaturalVoxelIndexer(size);
		List<SavedTileState> palette = this.data.values().stream().distinct().collect(Collectors.toList());
		palette.remove(SavedTileState.air());
		palette.add(0, SavedTileState.air());

		CompactPalettedBitVector<SavedTileState> vector = new CompactPalettedBitVector<SavedTileState>(palette, indexer.getVolume());
		for(Map.Entry<BlockPos, SavedTileState> entry : this.data.entrySet())
		{
			BlockPos offset = entry.getKey().subtract(origin);
			int index = indexer.toIndex(offset);
			vector.set(index, entry.getValue());
		}
		return new StructureBlueprint(indexer, ImmutableSet.copyOf(palette), vector, size, this.data.size());
	}
}
