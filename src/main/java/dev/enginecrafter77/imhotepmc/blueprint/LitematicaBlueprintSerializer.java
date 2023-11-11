package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class LitematicaBlueprintSerializer implements NBTBlueprintSerializer {
	private final BlockMapper blockMapper;

	public LitematicaBlueprintSerializer(BlockMapper mapper)
	{
		this.blockMapper = mapper;
	}

	public LitematicaBlueprintSerializer()
	{
		this(BlockMapper.identity());
	}

	public NBTTagCompound serializeVector(Vec3i size)
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("x", size.getX());
		compound.setInteger("y", size.getY());
		compound.setInteger("z", size.getZ());
		return compound;
	}

	public NBTTagCompound createMetadataTag(StructureBlueprint blueprint)
	{
		NBTTagCompound tag = new NBTTagCompound();

		Instant created = Instant.now();
		tag.setLong("TimeCreated", created.toEpochMilli());
		tag.setLong("TimeModified", created.toEpochMilli());
		tag.setString("Description", "");
		tag.setTag("Size", this.serializeVector(blueprint.getSize()));
		tag.setInteger("RegionCount", 1);
		tag.setInteger("TotalBlocks", blueprint.getTotalBlocks());
		tag.setInteger("TotalVolume", blueprint.getTotalVolume());
		tag.setString("Author", "");
		tag.setString("Name", "");

		return tag;
	}

	public NBTTagList serializeTileEntities(StructureBlueprint blueprint)
	{
		NBTTagList list = new NBTTagList();

		for(Map.Entry<Vec3i, StructureBlockSavedData> entry : blueprint.getStructureBlocks().entrySet())
		{
			Vec3i offset = entry.getKey();
			StructureBlockSavedData blockSavedData = entry.getValue();
			NBTTagCompound tileEntity = blockSavedData.getTileEntity();
			if(tileEntity == null)
				continue;
			tileEntity = tileEntity.copy();
			tileEntity.setInteger("x", offset.getX());
			tileEntity.setInteger("y", offset.getY());
			tileEntity.setInteger("z", offset.getZ());
			list.appendTag(tileEntity);
		}

		return list;
	}

	public NBTTagCompound createRegionTag(StructureBlueprint blueprint)
	{
		NBTTagCompound region = new NBTTagCompound();

		region.setTag("Position", this.serializeVector(Vec3i.NULL_VECTOR));
		region.setTag("Size", this.serializeVector(blueprint.getSize()));
		region.setTag("PendingFluidTicks", new NBTTagList());
		region.setTag("Entities", new NBTTagList());
		region.setTag("PendingBlockTicks", new NBTTagList());
		region.setTag("TileEntities", this.serializeTileEntities(blueprint));

		List<StructureBlockSavedData> unique = blueprint.getStructureBlocks().values().stream().distinct().collect(Collectors.toList());

		StructureBlockSavedData air = new StructureBlockSavedData(Blocks.AIR.getDefaultState(), null);
		unique.remove(air);
		unique.add(0, air);

		NBTTagList palette = new NBTTagList();
		for(StructureBlockSavedData paletteEntry : unique)
		{
			NBTTagCompound entryTag = new NBTTagCompound();
			entryTag.setString("Name", paletteEntry.getBlockState().getBlock().getRegistryName().toString());
			palette.appendTag(entryTag);
		}
		region.setTag("BlockStatePalette", palette);

		Vec3i size = blueprint.getSize();
		int vecSize = size.getX() * size.getY() * size.getZ();
		CompactPalettedBitVector<StructureBlockSavedData> vector = new CompactPalettedBitVector<StructureBlockSavedData>(unique, vecSize);
		BlockPosIndexer indexer = new LitematicaBlockPosIndexer(size);

		for(int index = 0; index < vector.getLength(); ++index)
		{
			BlockPos pos = indexer.fromIndex(index);
			StructureBlockSavedData block = blueprint.getStructureBlocks().get(pos);
			if(block != null)
				vector.set(index, block);
		}

		NBTTagLongArray stateArrayTag = vector.serializeNBT();
		region.setTag("BlockStates", stateArrayTag);

		return region;
	}

	@Override
	public NBTTagCompound serializeBlueprint(StructureBlueprint blueprint)
	{
		NBTTagCompound root = new NBTTagCompound();

		root.setInteger("MinecraftDataVersion", 3105);
		root.setInteger("Version", 5);
		root.setTag("Metadata", this.createMetadataTag(blueprint));

		NBTTagCompound regions = new NBTTagCompound();
		regions.setTag("Unnamed", this.createRegionTag(blueprint));
		root.setTag("Regions", regions);

		return root;
	}

	public Vec3i parseVector(NBTTagCompound compound)
	{
		int x = compound.getInteger("x");
		int y = compound.getInteger("y");
		int z = compound.getInteger("z");
		return new Vec3i(x, y, z);
	}

	@Override
	public StructureBlueprint deserializeBlueprint(NBTTagCompound source)
	{
		NBTTagCompound regions = source.getCompoundTag("Regions");
		NBTTagCompound mainRegion = regions.getCompoundTag("Unnamed");

		Vec3i size = this.absolutizeVector(this.parseVector(mainRegion.getCompoundTag("Size")));

		NBTTagList paletteTag = mainRegion.getTagList("BlockStatePalette", 10);
		List<StructureBlockSavedData> paletteList = new ArrayList<StructureBlockSavedData>(paletteTag.tagCount());
		Set<StructureBlockSavedData> paletteSet = new HashSet<StructureBlockSavedData>();

		for(int index = 0; index < paletteTag.tagCount(); ++index)
		{
			IBlockState state = this.readStateFromTag(paletteTag.getCompoundTagAt(index));
			if(state == null)
				continue;
			StructureBlockSavedData savedData = new StructureBlockSavedData(state, null);
			if(paletteSet.contains(savedData))
				continue;
			paletteList.add(savedData);
			paletteSet.add(savedData);
		}

		NBTTagLongArray arrayTag = (NBTTagLongArray)mainRegion.getTag("BlockStates");
		CompactPalettedBitVector<StructureBlockSavedData> vector = CompactPalettedBitVector.readFromNBT(paletteList, arrayTag);
		BlockPosIndexer indexer = new LitematicaBlockPosIndexer(size);

		StructureBlueprint.Builder builder = new StructureBlueprint.Builder();
		for(int index = 0; index < vector.getLength(); ++index)
		{
			StructureBlockSavedData block = vector.get(index);
			if(block.getBlockState().getBlock() == Blocks.AIR)
				continue;
			BlockPos pos = indexer.fromIndex(index);
			builder.addBlock(pos, block);
		}

		NBTTagList tileEntities = mainRegion.getTagList("TileEntities", 10);
		for(int index = 0; index < tileEntities.tagCount(); ++index)
		{
			NBTTagCompound tileTag = tileEntities.getCompoundTagAt(index);
			BlockPos pos = new BlockPos(this.parseVector(tileTag)); // Position is embedded in the tile entity data
			int vectorIndex = indexer.toIndex(pos);
			StructureBlockSavedData data = vector.get(vectorIndex);
			data = new StructureBlockSavedData(data.getBlockState(), tileTag);
			builder.addBlock(pos, data);
		}

		return builder.build();
	}

	private Vec3i absolutizeVector(Vec3i other)
	{
		return new Vec3i(Math.abs(other.getX()), Math.abs(other.getY()), Math.abs(other.getZ()));
	}

	@Nullable
	private IBlockState readStateFromTag(NBTTagCompound tag)
	{
		if(tag.hasKey("Name"))
		{
			ResourceLocation name = new ResourceLocation(tag.getString("Name"));
			ResourceLocation translatedName = this.blockMapper.translate(name);
			if(translatedName == null)
				return null;
			tag = tag.copy();
			tag.setString("Name", translatedName.toString());
		}
		return NBTUtil.readBlockState(tag);
	}
}
