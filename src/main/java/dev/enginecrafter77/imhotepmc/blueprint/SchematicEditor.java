package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslation;
import dev.enginecrafter77.imhotepmc.blueprint.translate.DataVersionTranslationTable;
import dev.enginecrafter77.imhotepmc.blueprint.translate.TranslationNotAvailableException;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.HashMap;
import java.util.Map;

public class SchematicEditor {
	private final Map<String, SchematicBlueprint.SchematicRegionBlueprint> regions;
	private final MutableSchematicMetadata metadata;

	private int blockCount;
	private Vec3i size;
	private int dataVersion;

	public SchematicEditor()
	{
		this.regions = new HashMap<String, SchematicBlueprint.SchematicRegionBlueprint>();
		this.metadata = new MutableSchematicMetadata();
		this.dataVersion = ImhotepMod.GAME_DATA_VERSION;
		this.size = Vec3i.NULL_VECTOR;
		this.blockCount = 0;
	}

	public SchematicEditor(SchematicBlueprint src)
	{
		this();
		this.size = src.getSize();
		this.regions.putAll(src.regions);
		this.metadata.set(src);
		this.dataVersion = src.getDataVersion();
	}

	public SchematicEditor setMetadata(SchematicMetadata metadata)
	{
		this.metadata.set(metadata);
		return this;
	}

	public SchematicEditor addRegion(String name, StructureBlueprint blueprint, BlockPos offset)
	{
		if(this.regions.isEmpty())
		{
			this.dataVersion = blueprint.getDataVersion();
		}
		else if(this.dataVersion != blueprint.getDataVersion())
		{
			throw new IllegalArgumentException(String.format("Attempting to add incompatible region with version %d to schematic containing regions with version %d", blueprint.getDataVersion(), this.dataVersion));
		}

		SchematicBlueprint.SchematicRegionBlueprint offsetBlueprint = new SchematicBlueprint.SchematicRegionBlueprint(blueprint, offset);
		BlockSelectionBox newRegionBox = new BlockSelectionBox();
		offsetBlueprint.computeBoundingBox(newRegionBox);

		BlockSelectionBox totalBox = new BlockSelectionBox();

		BlockSelectionBox regionBox = new BlockSelectionBox();
		for(SchematicBlueprint.SchematicRegionBlueprint region : this.regions.values())
		{
			region.computeBoundingBox(regionBox);
			regionBox.intersect(newRegionBox);
			if(regionBox.getVolume() > 0)
				throw new IllegalArgumentException("Regions cannot overlap!");
			totalBox.union(regionBox);
		}
		totalBox.union(newRegionBox);

		this.regions.put(name, offsetBlueprint);
		this.size = totalBox.getSize();
		this.blockCount += offsetBlueprint.getDefinedBlockCount();

		return this;
	}

	public SchematicEditor addRegionsFrom(SchematicBlueprint other)
	{
		for(String regionName : other.getRegions())
		{
			SchematicBlueprint.SchematicRegionBlueprint blueprint = other.getRegion(regionName);
			this.addRegion(regionName, blueprint.getRegionBlueprint(), blueprint.getOriginOffset());
		}
		return this;
	}

	public SchematicEditor translate(BlueprintTranslation translation)
	{
		for(String regionName : this.regions.keySet())
		{
			SchematicBlueprint.SchematicRegionBlueprint region = this.regions.get(regionName);
			BlueprintEditor editor = region.getRegionBlueprint().edit();
			editor.translate(translation);
			StructureBlueprint filtered = editor.build();
			SchematicBlueprint.SchematicRegionBlueprint newRegion = new SchematicBlueprint.SchematicRegionBlueprint(filtered, region.getOriginOffset());
			this.regions.put(regionName, newRegion);
		}
		return this;
	}

	public SchematicEditor translateVersion(DataVersionTranslationTable table) throws TranslationNotAvailableException
	{
		int desiredVersion = table.getProducedDataVersion();
		if(this.dataVersion == desiredVersion)
			return this;

		for(String regionName : this.regions.keySet())
		{
			SchematicBlueprint.SchematicRegionBlueprint region = this.regions.get(regionName);
			BlueprintEditor editor = region.getRegionBlueprint().edit();
			editor.translateVersion(table);
			StructureBlueprint filtered = editor.build();
			SchematicBlueprint.SchematicRegionBlueprint newRegion = new SchematicBlueprint.SchematicRegionBlueprint(filtered, region.getOriginOffset());
			this.regions.put(regionName, newRegion);
		}

		this.dataVersion = desiredVersion;
		return this;
	}

	public SchematicBlueprint build()
	{
		MutableSchematicMetadata meta = this.metadata.copy();
		meta.setSize(this.size);
		meta.setDefinedBlockCount(this.blockCount);
		meta.setRegionCount(this.regions.size());
		return new SchematicBlueprint(meta, ImmutableMap.copyOf(this.regions), this.dataVersion);
	}
}
