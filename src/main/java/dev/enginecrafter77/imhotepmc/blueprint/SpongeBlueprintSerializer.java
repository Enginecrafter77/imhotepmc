package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintCrossVersionTable;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintCrossVersionTranslation;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class SpongeBlueprintSerializer implements NBTBlueprintSerializer {
	private static final String NBT_KEY_VERSION = "Version";

	private final Version2 sv2;

	public SpongeBlueprintSerializer(@Nullable BlueprintCrossVersionTable table)
	{
		this.sv2 = new Version2(table);
	}

	@Override
	public NBTTagCompound serializeBlueprint(SchematicBlueprint blueprint)
	{
		NBTTagCompound tag = this.sv2.serializeBlueprint(blueprint);
		tag.setInteger(NBT_KEY_VERSION, 2);
		return tag;
	}

	@Override
	public SchematicBlueprint deserializeBlueprint(NBTTagCompound source)
	{
		int version = source.getInteger(NBT_KEY_VERSION);
		if(version == 2)
			return this.sv2.deserializeBlueprint(source);
		else
			throw new UnsupportedOperationException("Currently only v2 sponge schematics are supported. Sorry.");
	}

	@Override
	public SchematicMetadata deserializeBlueprintMetadata(NBTTagCompound source)
	{
		int version = source.getInteger(NBT_KEY_VERSION);
		if(version == 2)
			return this.sv2.deserializeBlueprintMetadata(source);
		else
			throw new UnsupportedOperationException("Currently only v2 sponge schematics are supported. Sorry.");
	}

	public static byte[] packSpongeVarintArray(int[] ids)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int out;
		for(int member : ids)
		{
			while(member > 127)
			{
				out = (member & 0x7F);
				out |= 0x80; // mark as non-terminal
				bos.write(out);
				member >>>= 7;
			}
			bos.write(member & 0x7F); // mark as terminal (by leaving 0x80 bit 0)
		}
		return bos.toByteArray();
	}

	public static int[] unpackSpongeVarintArray(byte[] src, @Nullable Integer expectedCapacity)
	{
		if(expectedCapacity == null)
			expectedCapacity = IntArrayList.DEFAULT_INITIAL_CAPACITY;
		IntList out = new IntArrayList(expectedCapacity);

		ByteArrayInputStream bis = new ByteArrayInputStream(src);
		int read; // The read byte
		int val = 0; // The value holder
		int chunk = 0; // The index of the 7-bit part in the integer
		while((read = bis.read()) != -1)
		{
			byte bt = (byte)(read & 0xFF);

			val |= (bt & 0x7F) << (chunk++ * 7);

			boolean terminal = (bt & 0x80) == 0; // MSB bit == continue
			if(terminal)
			{
				out.add(val);
				val = 0;
				chunk = 0;
			}
		}

		return out.toIntArray();
	}

	public static class Version2 implements NBTBlueprintSerializer
	{
		private static final String NBT_KEY_SIZE_X = "Width";
		private static final String NBT_KEY_SIZE_Y = "Height";
		private static final String NBT_KEY_SIZE_Z = "Length";
		private static final String NBT_KEY_METADATA = "Metadata";
		private static final String NBT_KEY_PALETTE = "Palette";
		private static final String NBT_KEY_BLOCKS = "BlockData";
		private static final String NBT_KEY_TE = "BlockEntities";
		private static final String NBT_KEY_VERSION = "DataVersion";
		private static final String NBT_KEY_METADATA_NAME = "Name";
		private static final String NBT_KEY_METADATA_AUTHOR = "Author";
		private static final String NBT_KEY_METADATA_DATE = "Date";

		@Nullable
		private final BlueprintCrossVersionTable table;

		public Version2(@Nullable BlueprintCrossVersionTable table)
		{
			this.table = table;
		}

		@Override
		public NBTTagCompound serializeBlueprint(SchematicBlueprint blueprint)
		{
			NBTTagCompound out = new NBTTagCompound();

			NBTTagCompound metadata = new NBTTagCompound();
			metadata.setString(NBT_KEY_METADATA_NAME, blueprint.getName());
			metadata.setString(NBT_KEY_METADATA_AUTHOR, blueprint.getAuthor());
			metadata.setLong(NBT_KEY_METADATA_DATE, blueprint.getCreateTime().toEpochMilli());
			out.setTag(NBT_KEY_METADATA, metadata);

			Vec3i size = blueprint.getSize();
			out.setShort(NBT_KEY_SIZE_X, (short)size.getX());
			out.setShort(NBT_KEY_SIZE_Y, (short)size.getY());
			out.setShort(NBT_KEY_SIZE_Z, (short)size.getZ());

			List<SavedBlockState> unique = blueprint.palette().stream().map(SavedBlockState::copyOf).distinct().collect(Collectors.toList());
			SavedBlockState air = SavedBlockState.ofBlock(Blocks.AIR);
			unique.remove(air);
			unique.add(0, air);

			NBTTagCompound paletteTag = new NBTTagCompound();
			for(int index = 0; index < unique.size(); ++index)
				paletteTag.setInteger(unique.get(index).toString(), index);
			out.setTag(NBT_KEY_PALETTE, paletteTag);

			VoxelIndexer indexer = new NaturalVoxelIndexer(BlockPos.ORIGIN, size);
			int[] indices = new int[indexer.getVolume()];
			NBTTagList tiles = new NBTTagList();

			BlueprintReader reader = blueprint.reader();
			while(reader.hasNext())
			{
				BlueprintVoxel voxel = reader.next();
				BlockPos pos = voxel.getPosition();
				int index = indexer.toIndex(pos);
				BlueprintEntry entry = voxel.getBlueprintEntry();

				SavedBlockState sts = SavedBlockState.copyOf(entry);
				int paletteIndex = unique.indexOf(sts);
				indices[index] = paletteIndex;

				if(entry.hasTileEntity())
				{
					NBTTagCompound tileTag = entry.getTileEntitySavedData().copy();
					tileTag.setInteger("X", pos.getX());
					tileTag.setInteger("Y", pos.getY());
					tileTag.setInteger("Z", pos.getZ());
					tiles.appendTag(tileTag);
				}
			}

			byte[] packedIndices = packSpongeVarintArray(indices);
			out.setByteArray(NBT_KEY_BLOCKS, packedIndices);
			out.setTag(NBT_KEY_TE, tiles);

			return out;
		}

		@Override
		public SchematicBlueprint deserializeBlueprint(NBTTagCompound source)
		{
			SchematicMetadata metadata = this.deserializeBlueprintMetadata(source);

			int version = source.getInteger(NBT_KEY_VERSION);

			Map<Integer, SavedBlockState> palette = new TreeMap<Integer, SavedBlockState>();

			NBTTagCompound paletteTag = source.getCompoundTag(NBT_KEY_PALETTE);
			for(String blockState : paletteTag.getKeySet())
			{
				int index = paletteTag.getInteger(blockState);
				palette.put(index, SavedBlockState.parse(blockState));
			}

			Vec3i size = metadata.getSize();
			VoxelIndexer indexer = new NaturalVoxelIndexer(BlockPos.ORIGIN, size);

			int[] blocks = unpackSpongeVarintArray(source.getByteArray(NBT_KEY_BLOCKS), indexer.getVolume());

			BlueprintEditor editor = new BlueprintEditor();
			for(int index = 0; index < indexer.getVolume(); ++index)
			{
				BlockPos pos = indexer.fromIndex(index);
				int val = blocks[index];
				editor.addBlock(pos, palette.get(val));
			}

			if(source.hasKey(NBT_KEY_TE))
			{
				NBTTagList tiles = source.getTagList(NBT_KEY_TE, 10);
				for(NBTBase tileEntryBase : tiles)
				{
					NBTTagCompound tileEntryTag = (NBTTagCompound)tileEntryBase;
					BlockPos pos = NBTUtil.getPosFromTag(tileEntryTag);
					editor.addTileEntity(pos, tileEntryTag);
				}
			}

			if(this.table != null)
			{
				BlueprintCrossVersionTranslation translation = this.table.getTranslationFor(version);
				if(translation != null)
					editor.translate(translation);
			}

			return SchematicBlueprint.builder().addRegion("Main", editor.build(), BlockPos.ORIGIN).setMetadata(metadata).build();
		}

		@Override
		public SchematicMetadata deserializeBlueprintMetadata(NBTTagCompound source)
		{
			MutableSchematicMetadata metadata = new MutableSchematicMetadata();
			metadata.setSize(this.readSize(source));

			NBTTagCompound metaTag = source.getCompoundTag(NBT_KEY_METADATA);
			Instant date = Instant.ofEpochMilli(metaTag.getLong(NBT_KEY_METADATA_DATE));

			metadata.setName(metaTag.getString(NBT_KEY_METADATA_NAME));
			metadata.setAuthor(metaTag.getString(NBT_KEY_METADATA_AUTHOR));
			metadata.setCreateTime(date);
			metadata.setModifyTime(date);

			return metadata;
		}

		public Vec3i readSize(NBTTagCompound source)
		{
			int x = source.getShort(NBT_KEY_SIZE_X);
			int y = source.getShort(NBT_KEY_SIZE_Y);
			int z = source.getShort(NBT_KEY_SIZE_Z);
			return new Vec3i(x, y, z);
		}
	}

}
