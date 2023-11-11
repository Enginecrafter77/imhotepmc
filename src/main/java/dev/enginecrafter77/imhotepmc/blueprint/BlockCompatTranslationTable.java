package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockCompatTranslationTable implements BlockMapper {
	private final Map<ResourceLocation, ResourceLocation> mappingTable;

	public BlockCompatTranslationTable(Map<ResourceLocation, ResourceLocation> map)
	{
		this.mappingTable = map;
	}

	@Nullable
	@Override
	public ResourceLocation translate(ResourceLocation source)
	{
		ResourceLocation tr = this.mappingTable.get(source);
		if(tr == null)
			tr = source;
		return tr;
	}

	private static BlockCompatTranslationTable INSTANCE = null;
	public static BlockCompatTranslationTable getInstance()
	{
		if(INSTANCE == null)
			INSTANCE = createDefaultCompatTranslationTable();
		return INSTANCE;
	}

	private static BlockCompatTranslationTable createDefaultCompatTranslationTable()
	{
		ImmutableMap.Builder<ResourceLocation, ResourceLocation> builder = ImmutableMap.builder();

		builder.put(new ResourceLocation("minecraft:oak_planks"), new ResourceLocation("minecraft:planks"));
		builder.put(new ResourceLocation("minecraft:oak_fence"), new ResourceLocation("minecraft:fence"));
		builder.put(new ResourceLocation("minecraft:oak_sign"), new ResourceLocation("minecraft:standing_sign"));

		return new BlockCompatTranslationTable(builder.build());
	}
}
