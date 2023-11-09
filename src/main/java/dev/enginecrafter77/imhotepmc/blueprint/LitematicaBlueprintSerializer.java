package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LitematicaBlueprintSerializer implements NBTBlueprintSerializer {
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

		int paletteLength = unique.size();
		int bitCount = (int)Math.ceil(Math.log(paletteLength) / Math.log(2));
		int arraySize = (int)Math.ceil((double)blueprint.getTotalBlocks() / (double)bitCount / (float)(Long.BYTES * 8));

		long[] stateArray = new long[arraySize];
		int slotIndex, shift;
		int entryIndex = 0;

		int entriesPerSlot = (Long.BYTES * 8) / bitCount;
		long mask = ~(-1L << bitCount);

		Vec3i size = blueprint.getSize();
		BlockPos last = new BlockPos(size.getX() - 1, size.getY() - 1, size.getZ() - 1);
		for(BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(BlockPos.ORIGIN, new BlockPos(last)))
		{
			StructureBlockSavedData block = blueprint.getStructureBlocks().get(pos);
			long paletteIndex = 0;
			if(block != null)
				paletteIndex = unique.indexOf(block);

			slotIndex = entryIndex / entriesPerSlot;
			shift = (entriesPerSlot - 1 - (entryIndex % entriesPerSlot)) * bitCount;
			stateArray[slotIndex] |= ((paletteIndex & mask) << shift);
			++entryIndex;
		}

		NBTTagLongArray stateArrayTag = new NBTTagLongArray(stateArray);
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
		regions.setTag("Main", this.createRegionTag(blueprint));
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
		Field DATA_FIELD = ObfuscationReflectionHelper.findField(NBTTagLongArray.class, "data"); // field_193587_b

		NBTTagCompound regions = source.getCompoundTag("Regions");
		NBTTagCompound mainRegion = regions.getCompoundTag("Main");

		Vec3i size = this.parseVector(mainRegion.getCompoundTag("Size"));

		NBTTagList paletteTag = mainRegion.getTagList("BlockStatePalette", 10);
		List<StructureBlockSavedData> paletteList = new ArrayList<StructureBlockSavedData>(paletteTag.tagCount());

		for(int index = 0; index < paletteTag.tagCount(); ++index)
		{
			NBTTagCompound paletteTagEntry = paletteTag.getCompoundTagAt(index);
			Block block = Block.getBlockFromName(paletteTagEntry.getString("Name"));
			StructureBlockSavedData savedData = new StructureBlockSavedData(block.getDefaultState(), null);
			paletteList.add(savedData);
		}

		int paletteLength = paletteList.size();
		int bitCount = (int)Math.ceil(Math.log(paletteLength) / Math.log(2));

		StructureBlueprint.Builder builder = new StructureBlueprint.Builder();

		NBTTagLongArray arrayTag = (NBTTagLongArray)mainRegion.getTag("BlockStates");
		try
		{
			long[] arr = (long[])DATA_FIELD.get(arrayTag);
			int slotIndex, shift;
			int entryIndex = 0;

			int entriesPerSlot = (Long.BYTES * 8) / bitCount;
			long mask = ~(-1L << bitCount);

			BlockPos last = new BlockPos(size.getX() - 1, size.getY() - 1, size.getZ() - 1);
			for(BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(BlockPos.ORIGIN, last))
			{
				slotIndex = entryIndex / entriesPerSlot;
				shift = (entriesPerSlot - 1 - (entryIndex % entriesPerSlot)) * bitCount;
				int paletteIndex = (int)((arr[slotIndex] >> shift) & mask);
				if(paletteIndex != 0)
					builder.addBlock(pos.toImmutable(), paletteList.get(paletteIndex));
				++entryIndex;
			}
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		return builder.build();
	}
}
