package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockRecordCompatTranslationTable implements BlockRecordMapper {
	private final Map<ResourceLocation, ResourceLocation> mappingTable;

	public BlockRecordCompatTranslationTable(Map<ResourceLocation, ResourceLocation> map)
	{
		this.mappingTable = map;
	}

	@Nullable
	@Override
	public SavedTileState translate(SavedTileState state)
	{
		ResourceLocation tr = this.mappingTable.get(state.getSavedBlockState().getBlockName());
		if(tr != null)
			state = new SavedTileState(new SavedBlockState(tr, state.getSavedBlockState().getBlockProperties()), null);
		return state;
	}

	private static BlockRecordCompatTranslationTable INSTANCE = null;
	public static BlockRecordCompatTranslationTable getInstance()
	{
		if(INSTANCE == null)
			INSTANCE = createDefaultCompatTranslationTable();
		return INSTANCE;
	}

	private static BlockRecordCompatTranslationTable createDefaultCompatTranslationTable()
	{
		ImmutableMap.Builder<ResourceLocation, ResourceLocation> builder = ImmutableMap.builder();

		builder.put(new ResourceLocation("minecraft:oak_planks"), new ResourceLocation("minecraft:planks"));
		builder.put(new ResourceLocation("minecraft:oak_fence"), new ResourceLocation("minecraft:fence"));
		builder.put(new ResourceLocation("minecraft:oak_sign"), new ResourceLocation("minecraft:standing_sign"));

		return new BlockRecordCompatTranslationTable(builder.build());
	}
}
