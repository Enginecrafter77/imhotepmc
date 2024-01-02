package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SchematicaBlueprintSerializer implements NBTBlueprintSerializer {
	private static final Log LOGGER = LogFactory.getLog(SchematicaBlueprintSerializer.class);

	private static final String NBT_KEY_SIZE_X = "Width";
	private static final String NBT_KEY_SIZE_Y = "Height";
	private static final String NBT_KEY_SIZE_Z = "Length";
	private static final String NBT_KEY_BLOCKS = "Blocks";
	private static final String NBT_KEY_META = "Data";
	private static final String NBT_KEY_TE = "TileEntities";

	private static final String MAIN_REGION_NAME = "Main";

	@Override
	public NBTTagCompound serializeBlueprint(SchematicBlueprint blueprint)
	{
		Vec3i size = blueprint.getSize();
		VoxelIndexer indexer = new NaturalVoxelIndexer(BlockPos.ORIGIN, size);

		byte[] blks = new byte[indexer.getVolume()];
		byte[] meta = new byte[indexer.getVolume()];
		NBTTagList tiles = new NBTTagList();

		BlueprintReader reader = blueprint.reader();
		while(reader.hasNext())
		{
			BlueprintVoxel voxel = reader.next();
			int index = indexer.toIndex(voxel.getPosition());
			IBlockState state = voxel.getBlueprintEntry().createBlockState();
			if(state == null)
			{
				LOGGER.error("Unable to synthesize block state for " + voxel);
				continue;
			}

			Block blk = state.getBlock();
			blks[index] = (byte)(Block.getIdFromBlock(blk) & 0xFF);
			meta[index] = (byte)(blk.getMetaFromState(state) & 0x0F);

			NBTTagCompound tile = voxel.getBlueprintEntry().getTileEntitySavedData();
			if(tile != null)
				tiles.appendTag(tile);
		}

		NBTTagCompound out = new NBTTagCompound();
		out.setShort(NBT_KEY_SIZE_X, (short)size.getX());
		out.setShort(NBT_KEY_SIZE_Y, (short)size.getY());
		out.setShort(NBT_KEY_SIZE_Z, (short)size.getZ());
		out.setByteArray(NBT_KEY_BLOCKS, blks);
		out.setByteArray(NBT_KEY_META, meta);
		out.setTag(NBT_KEY_TE, tiles);
		return out;
	}

	@Override
	public SchematicBlueprint deserializeBlueprint(NBTTagCompound source)
	{
		SchematicMetadata metadata = this.deserializeBlueprintMetadata(source);
		Vec3i size = metadata.getSize();
		VoxelIndexer indexer = new NaturalVoxelIndexer(BlockPos.ORIGIN, size);

		byte[] blks = source.getByteArray(NBT_KEY_BLOCKS);
		byte[] meta = source.getByteArray(NBT_KEY_META);

		BlueprintEditor editor = new BlueprintEditor();
		for(int index = 0; index < indexer.getVolume(); ++index)
		{
			BlockPos pos = indexer.fromIndex(index);
			Block blk = Block.getBlockById(blks[index]);
			IBlockState state = blk.getStateFromMeta(meta[index]);
			editor.addBlock(pos, SavedTileState.fromBlockState(state));
		}

		for(NBTBase tileTagBase : source.getTagList(NBT_KEY_TE, 10))
		{
			NBTTagCompound tileTag = (NBTTagCompound)tileTagBase;
			BlockPos pos = NBTUtil.getPosFromTag(tileTag);
			editor.addTileEntity(pos, tileTag);
		}

		StructureBlueprint blueprint = editor.build();

		return SchematicBlueprint.builder().addRegion(MAIN_REGION_NAME, blueprint, BlockPos.ORIGIN).setMetadata(metadata).build();
	}

	@Override
	public SchematicMetadata deserializeBlueprintMetadata(NBTTagCompound source)
	{
		MutableSchematicMetadata metadata = new MutableSchematicMetadata();
		metadata.setSize(this.readSize(source));
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
