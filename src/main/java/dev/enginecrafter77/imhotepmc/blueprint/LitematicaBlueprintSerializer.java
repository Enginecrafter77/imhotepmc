package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslation;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LitematicaBlueprintSerializer implements NBTBlueprintSerializer {
	private final BlueprintTranslation blueprintTranslation;

	private static final String NBT_KEY_MCDATAVERSION = "MinecraftDataVersion";
	private static final String NBT_KEY_VERSION = "Version";
	private static final String NBT_KEY_METADATA = "Metadata";
	private static final String NBT_KEY_REGIONS = "Regions";

	private static final String NBT_KEY_META_NAME = "Name";
	private static final String NBT_KEY_META_AUTHOR = "Author";
	private static final String NBT_KEY_META_DESCRIPTION = "Description";
	private static final String NBT_KEY_META_TIME_CREATED = "TimeCreated";
	private static final String NBT_KEY_META_TIME_MODIFIED = "TimeModified";
	private static final String NBT_KEY_META_SIZE = "EnclosingSize";
	private static final String NBT_KEY_META_BLOCK_COUNT = "TotalBlocks";
	private static final String NBT_KEY_META_VOLUME = "TotalVolume";
	private static final String NBT_KEY_META_REGION_COUNT = "RegionCount";

	private static final String NBT_KEY_REGION_OFFSET = "Position";
	private static final String NBT_KEY_REGION_SIZE = "Size";
	private static final String NBT_KEY_REGION_PALETTE = "BlockStatePalette";
	private static final String NBT_KEY_REGION_BLOCKMAP = "BlockStates";
	private static final String NBT_KEY_REGION_TILE_ENTITIES = "TileEntities";
	private static final String NBT_KEY_REGION_PENDING_TILE_TICKS = "PendingBlockTicks";
	private static final String NBT_KEY_REGION_PENDING_FLUID_TICKS = "PendingFluidTicks";
	private static final String NBT_KEY_REGION_ENTITIES = "Entities";

	public LitematicaBlueprintSerializer(BlueprintTranslation mapper)
	{
		this.blueprintTranslation = mapper;
	}

	public LitematicaBlueprintSerializer()
	{
		this(BlueprintTranslation.identity());
	}

	protected void serializeVectorInto(Vec3i vector, NBTTagCompound tag)
	{
		tag.setInteger("x", vector.getX());
		tag.setInteger("y", vector.getY());
		tag.setInteger("z", vector.getZ());
	}

	protected NBTTagCompound serializeVector(Vec3i vector)
	{
		NBTTagCompound compound = new NBTTagCompound();
		this.serializeVectorInto(vector, compound);
		return compound;
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

	protected NBTTagCompound createMetadataTag(SchematicBlueprint blueprint)
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString(NBT_KEY_META_NAME, blueprint.getName());
		tag.setLong(NBT_KEY_META_TIME_CREATED, blueprint.getCreateTime().toEpochMilli());
		tag.setLong(NBT_KEY_META_TIME_MODIFIED, blueprint.getModifyTime().toEpochMilli());
		tag.setString(NBT_KEY_META_DESCRIPTION, blueprint.getDescription());
		tag.setString(NBT_KEY_META_AUTHOR, blueprint.getAuthor());
		tag.setTag(NBT_KEY_META_SIZE, this.serializeVector(blueprint.getSize()));
		tag.setInteger(NBT_KEY_META_REGION_COUNT, blueprint.getRegionCount());
		tag.setInteger(NBT_KEY_META_BLOCK_COUNT, blueprint.getBlockCount());
		tag.setInteger(NBT_KEY_META_VOLUME, blueprint.getTotalVolume());

		return tag;
	}

	protected NBTTagCompound createRegionTag(SchematicBlueprint.OffsetRegionBlueprint blueprint)
	{
		NBTTagCompound region = new NBTTagCompound();

		region.setTag(NBT_KEY_REGION_OFFSET, this.serializeVector(blueprint.getOrigin()));
		region.setTag(NBT_KEY_REGION_SIZE, this.serializeVector(blueprint.getSize()));
		region.setTag(NBT_KEY_REGION_PENDING_FLUID_TICKS, new NBTTagList());
		region.setTag(NBT_KEY_REGION_ENTITIES, new NBTTagList());
		region.setTag(NBT_KEY_REGION_PENDING_TILE_TICKS, new NBTTagList());

		List<SavedBlockState> unique = blueprint.getRegionBlueprint()
				.getStructureBlocks()
				.values()
				.stream()
				.map(SavedTileState::getSavedBlockState)
				.distinct()
				.collect(Collectors.toList());

		SavedBlockState air = SavedBlockState.ofBlock(Blocks.AIR);
		unique.remove(air);
		unique.add(0, air);

		NBTTagList palette = new NBTTagList();
		unique.stream().map(SavedBlockState::serialize).forEach(palette::appendTag);
		region.setTag(NBT_KEY_REGION_PALETTE, palette);

		Vec3i size = blueprint.getSize();
		int vecSize = size.getX() * size.getY() * size.getZ();
		CompactPalettedBitVector<SavedBlockState> vector = new CompactPalettedBitVector<SavedBlockState>(unique, vecSize);
		VoxelIndexer indexer = new NaturalVoxelIndexer(size);

		NBTTagList tileEntities = new NBTTagList();

		int entries = size.getX() * size.getY() * size.getZ();
		for(int index = 0; index < entries; ++index)
		{
			BlockPos pos = indexer.fromIndex(index);
			SavedTileState savedTileState = blueprint.getRegionBlueprint().getStructureBlocks().get(pos);
			if(savedTileState != null)
			{
				vector.set(index, savedTileState.getSavedBlockState());
				NBTTagCompound tileEntity = savedTileState.getTileEntity();
				if(tileEntity != null)
				{
					tileEntity = tileEntity.copy();
					this.serializeVectorInto(pos, tileEntity);
					tileEntities.appendTag(tileEntity);
				}
			}
		}

		NBTTagLongArray stateArrayTag = vector.serializeNBT();
		region.setTag(NBT_KEY_REGION_BLOCKMAP, stateArrayTag);
		region.setTag(NBT_KEY_REGION_TILE_ENTITIES, tileEntities);

		return region;
	}

	@Override
	public NBTTagCompound serializeBlueprint(SchematicBlueprint blueprint)
	{
		NBTTagCompound root = new NBTTagCompound();

		root.setInteger(NBT_KEY_MCDATAVERSION, 3105);
		root.setInteger(NBT_KEY_VERSION, 5);
		root.setTag(NBT_KEY_METADATA, this.createMetadataTag(blueprint));

		NBTTagCompound regions = new NBTTagCompound();
		for(String regionName : blueprint.getRegions())
		{
			SchematicBlueprint.OffsetRegionBlueprint region = blueprint.getRegion(regionName);
			regions.setTag(regionName, this.createRegionTag(region));
		}
		root.setTag(NBT_KEY_REGIONS, regions);

		return root;
	}

	@Override
	public SchematicBlueprint deserializeBlueprint(NBTTagCompound source)
	{
		SchematicBlueprint.Builder builder = SchematicBlueprint.builder();
		builder.setMetadata(this.deserializeBlueprintMetadata(source));

		NBTTagCompound regions = source.getCompoundTag(NBT_KEY_REGIONS);
		for(String name : regions.getKeySet())
		{
			NBTTagCompound regionTag = regions.getCompoundTag(name);
			RegionBlueprint region = deserializeRegionBlueprint(regionTag);
			BlockPos offset = new BlockPos(readVector(regionTag.getCompoundTag(NBT_KEY_REGION_OFFSET)));
			builder.addRegion(name, region, offset);
		}

		return builder.build();
	}

	@Override
	public SchematicMetadata deserializeBlueprintMetadata(NBTTagCompound source)
	{
		MutableSchematicMetadata metadata = new MutableSchematicMetadata();

		NBTTagCompound meta = source.getCompoundTag(NBT_KEY_METADATA);
		metadata.setCreateTime(Instant.ofEpochMilli(meta.getLong(NBT_KEY_META_TIME_CREATED)));
		metadata.setModifyTime(Instant.ofEpochMilli(meta.getLong(NBT_KEY_META_TIME_MODIFIED)));
		metadata.setAuthor(meta.getString(NBT_KEY_META_AUTHOR));
		metadata.setDescription(meta.getString(NBT_KEY_META_DESCRIPTION));
		metadata.setName(meta.getString(NBT_KEY_META_NAME));
		metadata.setBlockCount(meta.getInteger(NBT_KEY_META_BLOCK_COUNT));
		metadata.setSize(readVector(meta.getCompoundTag(NBT_KEY_META_SIZE)));

		metadata.setRegionCount(source.getCompoundTag(NBT_KEY_REGIONS).getSize());
		return metadata;
	}

	public RegionBlueprint deserializeRegionBlueprint(NBTTagCompound regionTag)
	{
		Vec3i size = absolutizeVector(readVector(regionTag.getCompoundTag(NBT_KEY_REGION_SIZE)));

		NBTTagList paletteTag = regionTag.getTagList(NBT_KEY_REGION_PALETTE, 10);
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

		NBTTagLongArray arrayTag = (NBTTagLongArray)regionTag.getTag(NBT_KEY_REGION_BLOCKMAP);
		CompactPalettedBitVector<SavedBlockState> vector = CompactPalettedBitVector.readFromNBT(paletteList, arrayTag);
		VoxelIndexer indexer = new NaturalVoxelIndexer(size);
		SavedBlockState air = paletteList.get(0);

		RegionBlueprint.Builder builder = RegionBlueprint.builder();
		builder.setSize(size);
		for(int index = 0; index < vector.getLength(); ++index)
		{
			SavedBlockState block = vector.get(index);
			if(block == air)
				continue;
			BlockPos pos = indexer.fromIndex(index);
			builder.addBlock(pos, block);
		}

		NBTTagList tileEntities = regionTag.getTagList(NBT_KEY_REGION_TILE_ENTITIES, 10);
		for(int index = 0; index < tileEntities.tagCount(); ++index)
		{
			NBTTagCompound tileTag = tileEntities.getCompoundTagAt(index);
			BlockPos pos = new BlockPos(readVector(tileTag));
			builder.addTileEntity(pos, tileTag);
		}

		builder.translate(this.blueprintTranslation);

		return builder.build();
	}
}
