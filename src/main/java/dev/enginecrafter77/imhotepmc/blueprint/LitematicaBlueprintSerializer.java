package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.util.Vector3i;
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

	protected NBTTagCompound createMetadataTag(SchematicBlueprint blueprint)
	{
		NBTTagCompound tag = new NBTTagCompound();

		Vec3i size = blueprint.getSize();
		int totalVolume = size.getX() * size.getY() * size.getZ();

		tag.setString(NBT_KEY_META_NAME, blueprint.getName());
		tag.setLong(NBT_KEY_META_TIME_CREATED, blueprint.getCreateTime().toEpochMilli());
		tag.setLong(NBT_KEY_META_TIME_MODIFIED, blueprint.getModifyTime().toEpochMilli());
		tag.setString(NBT_KEY_META_DESCRIPTION, blueprint.getDescription());
		tag.setString(NBT_KEY_META_AUTHOR, blueprint.getAuthor());
		tag.setTag(NBT_KEY_META_SIZE, this.serializeVector(size));
		tag.setInteger(NBT_KEY_META_REGION_COUNT, blueprint.getRegionCount());
		tag.setInteger(NBT_KEY_META_BLOCK_COUNT, blueprint.getDefinedBlockCount());
		tag.setInteger(NBT_KEY_META_VOLUME, totalVolume);

		return tag;
	}

	protected NBTTagCompound createRegionTag(SchematicBlueprint.SchematicRegionBlueprint blueprint)
	{
		NBTTagCompound region = new NBTTagCompound();

		region.setTag(NBT_KEY_REGION_OFFSET, this.serializeVector(blueprint.getOriginOffset()));
		region.setTag(NBT_KEY_REGION_SIZE, this.serializeVector(blueprint.getSize()));
		region.setTag(NBT_KEY_REGION_PENDING_FLUID_TICKS, new NBTTagList());
		region.setTag(NBT_KEY_REGION_ENTITIES, new NBTTagList());
		region.setTag(NBT_KEY_REGION_PENDING_TILE_TICKS, new NBTTagList());

		List<SavedBlockState> unique = blueprint.palette().stream().map(SavedBlockState::copyOf).distinct().collect(Collectors.toList());

		SavedBlockState air = SavedBlockState.ofBlock(Blocks.AIR);
		unique.remove(air);
		unique.add(0, air);

		NBTTagList palette = new NBTTagList();
		unique.stream().map(SavedBlockState::serialize).forEach(palette::appendTag);
		region.setTag(NBT_KEY_REGION_PALETTE, palette);

		Vec3i size = blueprint.getSize();
		int vecSize = size.getX() * size.getY() * size.getZ();
		CompactPalettedBitVector<SavedBlockState> vector = new CompactPalettedBitVector<SavedBlockState>(unique, vecSize);
		VoxelIndexer indexer = NaturalVoxelIndexer.inVolume(size);

		NBTTagList tileEntities = new NBTTagList();

		int entries = size.getX() * size.getY() * size.getZ();
		for(int index = 0; index < entries; ++index)
		{
			BlockPos relativePosition = indexer.fromIndex(index);
			BlueprintEntry savedTileState = blueprint.getBlockAt(relativePosition.add(blueprint.getOriginOffset()));
			vector.set(index, SavedBlockState.copyOf(savedTileState));
			NBTTagCompound tileEntity = savedTileState.getTileEntitySavedData();
			if(tileEntity != null)
			{
				tileEntity = tileEntity.copy();
				this.serializeVectorInto(relativePosition, tileEntity);
				tileEntities.appendTag(tileEntity);
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

		root.setInteger(NBT_KEY_MCDATAVERSION, blueprint.getDataVersion());
		root.setInteger(NBT_KEY_VERSION, 5);
		root.setTag(NBT_KEY_METADATA, this.createMetadataTag(blueprint));

		NBTTagCompound regions = new NBTTagCompound();
		for(String regionName : blueprint.getRegions())
		{
			SchematicBlueprint.SchematicRegionBlueprint region = blueprint.getRegion(regionName);
			regions.setTag(regionName, this.createRegionTag(region));
		}
		root.setTag(NBT_KEY_REGIONS, regions);

		return root;
	}

	@Override
	public SchematicBlueprint deserializeBlueprint(NBTTagCompound source)
	{
		SchematicEditor builder = SchematicBlueprint.builder();
		builder.setMetadata(this.deserializeBlueprintMetadata(source));

		int dataVersion = source.getInteger(NBT_KEY_MCDATAVERSION);

		Vector3i offset = new Vector3i();
		NBTTagCompound regions = source.getCompoundTag(NBT_KEY_REGIONS);
		for(String name : regions.getKeySet())
		{
			NBTTagCompound regionTag = regions.getCompoundTag(name);
			regionTag = this.normalizeRegion(regionTag);
			StructureBlueprint region = deserializeRegionBlueprint(regionTag, dataVersion);
			offset.deserializeNBT(regionTag.getCompoundTag(NBT_KEY_REGION_OFFSET));
			builder.addRegion(name, region, offset.toBlockPos());
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
		metadata.setDefinedBlockCount(meta.getInteger(NBT_KEY_META_BLOCK_COUNT));
		metadata.setSize(new Vector3i(meta.getCompoundTag(NBT_KEY_META_SIZE)).toVec3i());

		metadata.setRegionCount(source.getCompoundTag(NBT_KEY_REGIONS).getSize());
		return metadata;
	}

	public StructureBlueprint deserializeRegionBlueprint(NBTTagCompound regionTag, int version)
	{
		Vector3i size = new Vector3i(regionTag.getCompoundTag(NBT_KEY_REGION_SIZE));

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
		VoxelIndexer indexer = NaturalVoxelIndexer.inVolume(size.toVec3i());
		SavedBlockState air = paletteList.get(0);

		BlueprintEditor blueprintEditor = StructureBlueprint.begin();
		blueprintEditor.setDataVersion(version);
		blueprintEditor.setSize(size.toVec3i());
		for(int index = 0; index < indexer.getVolume(); ++index)
		{
			SavedBlockState block = vector.get(index);
			BlockPos pos = indexer.fromIndex(index);
			if(Objects.equals(block, air))
				continue;
			blueprintEditor.addBlock(pos, block);
		}

		Vector3i tilePos = new Vector3i();
		NBTTagList tileEntities = regionTag.getTagList(NBT_KEY_REGION_TILE_ENTITIES, 10);
		for(int index = 0; index < tileEntities.tagCount(); ++index)
		{
			NBTTagCompound tileTag = tileEntities.getCompoundTagAt(index);
			tilePos.deserializeNBT(tileTag);
			blueprintEditor.addTileEntity(tilePos.toBlockPos(), tileTag);
		}

		return blueprintEditor.build();
	}

	public NBTTagCompound normalizeRegion(NBTTagCompound regionTag)
	{
		Vector3i size = new Vector3i(regionTag.getCompoundTag(NBT_KEY_REGION_SIZE));
		Vector3i offset = new Vector3i(regionTag.getCompoundTag(NBT_KEY_REGION_OFFSET));
		this.normalizeRegionSizes(size, offset);
		regionTag.setTag(NBT_KEY_REGION_SIZE, size.serializeNBT());
		regionTag.setTag(NBT_KEY_REGION_OFFSET, offset.serializeNBT());
		return regionTag;
	}

	public void normalizeRegionSizes(Vector3i size, Vector3i offset)
	{
		if(size.x < 0)
		{
			size.x = Math.abs(size.x);
			offset.x -= size.x - 1;
		}

		if(size.y < 0)
		{
			size.y = Math.abs(size.y);
			offset.y -= size.y - 1;
		}

		if(size.z < 0)
		{
			size.z = Math.abs(size.z);
			offset.z -= size.z - 1;
		}
	}
}
