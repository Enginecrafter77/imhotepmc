package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslation;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class LitematicaBlueprintSerializer implements NBTBlueprintSerializer {
	private final BlueprintTranslation blueprintTranslation;

	public LitematicaBlueprintSerializer(BlueprintTranslation mapper)
	{
		this.blueprintTranslation = mapper;
	}

	public LitematicaBlueprintSerializer()
	{
		this(BlueprintTranslation.identity());
	}

	protected NBTTagCompound serializeVector(Vec3i size)
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("x", size.getX());
		compound.setInteger("y", size.getY());
		compound.setInteger("z", size.getZ());
		return compound;
	}

	protected NBTTagCompound createMetadataTag(SchematicBlueprint blueprint)
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setLong("TimeCreated", blueprint.getCreateTime().toEpochMilli());
		tag.setLong("TimeModified", blueprint.getModifyTime().toEpochMilli());
		tag.setString("Description", blueprint.getDescription());
		tag.setString("Author", blueprint.getAuthor());
		tag.setTag("Size", this.serializeVector(blueprint.getSize()));

		tag.setInteger("RegionCount", blueprint.getRegionCount());
		tag.setInteger("TotalBlocks", blueprint.getBlockCount());
		tag.setInteger("TotalVolume", blueprint.getTotalVolume());
		tag.setString("Name", blueprint.getName());

		return tag;
	}

	protected NBTTagCompound createRegionTag(RegionBlueprint blueprint)
	{
		NBTTagCompound region = new NBTTagCompound();

		region.setTag("Position", this.serializeVector(Vec3i.NULL_VECTOR));
		region.setTag("Size", this.serializeVector(blueprint.getSize()));
		region.setTag("PendingFluidTicks", new NBTTagList());
		region.setTag("Entities", new NBTTagList());
		region.setTag("PendingBlockTicks", new NBTTagList());

		List<SavedBlockState> unique = blueprint.getStructureBlocks()
				.values()
				.stream()
				.map(ResolvedBlueprintBlock::save)
				.map(SavedTileState::getSavedBlockState)
				.distinct()
				.collect(Collectors.toList());

		SavedBlockState air = SavedBlockState.ofBlock(Blocks.AIR);
		unique.remove(air);
		unique.add(0, air);

		NBTTagList palette = new NBTTagList();
		unique.stream().map(SavedBlockState::serialize).forEach(palette::appendTag);
		region.setTag("BlockStatePalette", palette);

		Vec3i size = blueprint.getSize();
		int vecSize = size.getX() * size.getY() * size.getZ();
		CompactPalettedBitVector<SavedBlockState> vector = new CompactPalettedBitVector<SavedBlockState>(unique, vecSize);
		VoxelIndexer indexer = new LitematicaVoxelIndexer(size);

		NBTTagList tileEntities = new NBTTagList();

		for(int index = 0; index < vector.getLength(); ++index)
		{
			BlockPos pos = indexer.fromIndex(index);
			ResolvedBlueprintBlock block = blueprint.getStructureBlocks().get(pos);
			if(block != null)
			{
				SavedTileState savedTileState = block.save();
				vector.set(index, savedTileState.getSavedBlockState());
				NBTTagCompound tileEntity = savedTileState.getTileEntity();
				if(tileEntity != null)
				{
					tileEntity = tileEntity.copy();
					tileEntity.setInteger("x", pos.getX());
					tileEntity.setInteger("y", pos.getY());
					tileEntity.setInteger("z", pos.getZ());
					tileEntities.appendTag(tileEntity);
				}
			}
		}

		NBTTagLongArray stateArrayTag = vector.serializeNBT();
		region.setTag("BlockStates", stateArrayTag);
		region.setTag("TileEntities", tileEntities);

		return region;
	}

	@Override
	public NBTTagCompound serializeBlueprint(SchematicBlueprint blueprint)
	{
		NBTTagCompound root = new NBTTagCompound();

		root.setInteger("MinecraftDataVersion", 3105);
		root.setInteger("Version", 5);
		root.setTag("Metadata", this.createMetadataTag(blueprint));

		NBTTagCompound regions = new NBTTagCompound();
		for(String regionName : blueprint.getRegions())
		{
			RegionBlueprint region = blueprint.getRegion(regionName).getRegionBlueprint();
			regions.setTag(regionName, this.createRegionTag(region));
		}
		root.setTag("Regions", regions);

		return root;
	}

	@Override
	public SchematicBlueprint deserializeBlueprint(NBTTagCompound source)
	{
		SchematicBlueprint blueprint = this.deserializeBlueprintMetadata(source);

		NBTTagCompound regions = source.getCompoundTag("Regions");
		for(String name : regions.getKeySet())
		{
			NBTTagCompound regionTag = regions.getCompoundTag(name);
			RegionBlueprint region = deserializeRegionBlueprint(regionTag);
			BlockPos offset = new BlockPos(readVector(regionTag.getCompoundTag("Position")));
			blueprint.addRegion(name, region, offset);
		}

		return blueprint;
	}

	@Override
	public SchematicBlueprint deserializeBlueprintMetadata(NBTTagCompound source)
	{
		SchematicBlueprint blueprint = new SchematicBlueprint();
		NBTTagCompound meta = source.getCompoundTag("Metadata");
		blueprint.setCreateTime(Instant.ofEpochMilli(meta.getLong("TimeCreated")));
		blueprint.setModifyTime(Instant.ofEpochMilli(meta.getLong("TimeModified")));
		blueprint.setAuthor(meta.getString("Author"));
		blueprint.setDescription(meta.getString("Description"));
		blueprint.setName(meta.getString("Name"));
		return blueprint;
	}

	public RegionBlueprint deserializeRegionBlueprint(NBTTagCompound regionTag)
	{
		Vec3i size = absolutizeVector(readVector(regionTag.getCompoundTag("Size")));

		NBTTagList paletteTag = regionTag.getTagList("BlockStatePalette", 10);
		List<SavedBlockState> paletteList = new ArrayList<SavedBlockState>(paletteTag.tagCount());
		Set<SavedBlockState> paletteSet = new HashSet<SavedBlockState>();

		for(int index = 0; index < paletteTag.tagCount(); ++index)
		{
			SavedBlockState savedData = SavedBlockState.deserialize(paletteTag.getCompoundTagAt(index));
			if(paletteSet.contains(savedData))
				continue;
			paletteList.add(savedData);
			paletteSet.add(savedData);
		}

		NBTTagLongArray arrayTag = (NBTTagLongArray)regionTag.getTag("BlockStates");
		CompactPalettedBitVector<SavedBlockState> vector = CompactPalettedBitVector.readFromNBT(paletteList, arrayTag);
		VoxelIndexer indexer = new LitematicaVoxelIndexer(size);
		SavedBlockState air = paletteList.get(0);

		RegionBlueprint.Builder builder = RegionBlueprint.builder();
		for(int index = 0; index < vector.getLength(); ++index)
		{
			SavedBlockState block = vector.get(index);
			if(block == air)
				continue;
			BlockPos pos = indexer.fromIndex(index);
			builder.addBlock(pos, block);
		}

		NBTTagList tileEntities = regionTag.getTagList("TileEntities", 10);
		for(int index = 0; index < tileEntities.tagCount(); ++index)
		{
			NBTTagCompound tileTag = tileEntities.getCompoundTagAt(index);
			BlockPos pos = new BlockPos(readVector(tileTag));
			builder.addTileEntity(pos, tileTag);
		}

		builder.translate(this.blueprintTranslation);

		return builder.build();
	}

	private static Vec3i readVector(NBTTagCompound tag)
	{
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		return new Vec3i(x, y, z);
	}

	private static Vec3i absolutizeVector(Vec3i other)
	{
		return new Vec3i(Math.abs(other.getX()), Math.abs(other.getY()), Math.abs(other.getZ()));
	}
}
