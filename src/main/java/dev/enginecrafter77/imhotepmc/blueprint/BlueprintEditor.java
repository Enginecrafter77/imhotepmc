package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableSet;
import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.translate.*;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
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

	private int dataVersion;

	@Nullable
	private Vec3i size;

	public BlueprintEditor()
	{
		this.data = new HashMap<BlockPos, SavedTileState>();
		this.dataVersion = ImhotepMod.GAME_DATA_VERSION;
		this.size = null;
	}

	public void importBlueprint(Blueprint other)
	{
		other.reader().forEachRemaining(this::addVoxel);
	}

	public void setDataVersion(int dataVersion)
	{
		this.dataVersion = dataVersion;
	}

	public void addVoxel(BlueprintVoxel voxel)
	{
		this.addBlock(voxel.getPosition().toImmutable(), voxel.getBlueprintEntry());
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

	public BlueprintEditor translateVersion(DataVersionTranslationTable table) throws TranslationNotAvailableException
	{
		int desiredVersion = table.getProducedDataVersion();
		if(this.dataVersion == desiredVersion)
			return this;

		DataVersionTranslation translation = table.getTranslationFor(this.dataVersion);
		if(translation == null)
			throw new TranslationNotAvailableException(this.dataVersion, desiredVersion);
		BlueprintTranslation translations = BlueprintTranslation.aggregate(translation.getBlueprintTranslations());
		this.translate(translations);
		this.dataVersion = desiredVersion;
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
			BlockSelectionBox box = new BlockSelectionBox();
			box.setToContain(this.data.keySet());
			origin = box.getMinCorner();
			size = box.getSize();
		}

		VoxelIndexer indexer = NaturalVoxelIndexer.inVolume(size);
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
		return new StructureBlueprint(indexer, ImmutableSet.copyOf(palette), vector, size, this.data.size(), this.dataVersion);
	}
}
