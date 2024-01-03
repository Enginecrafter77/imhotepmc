package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Set;

@Immutable
public class StructureBlueprint implements Blueprint {
	private final Set<SavedTileState> palette;
	private final CompactPalettedBitVector<SavedTileState> vector;
	private final VoxelIndexer indexer;
	private final int definedBlocks;
	private final Vec3i size;

	private final int version;

	protected StructureBlueprint(VoxelIndexer indexer, Set<SavedTileState> palette, CompactPalettedBitVector<SavedTileState> vector, Vec3i size, int definedBlocks, int version)
	{
		this.definedBlocks = definedBlocks;
		this.indexer = indexer;
		this.palette = palette;
		this.vector = vector;
		this.version = version;
		this.size = size;
	}

	@Override
	public int getDataVersion()
	{
		return this.version;
	}

	@Override
	public Vec3i getSize()
	{
		return this.size;
	}

	@Override
	public BlockPos getOriginOffset()
	{
		return BlockPos.ORIGIN;
	}

	@Override
	public BlueprintEntry getBlockAt(BlockPos position)
	{
		position = position.subtract(this.getOriginOffset());
		int index = this.indexer.toIndex(position);
		return this.vector.get(index);
	}

	@Override
	public int getDefinedBlockCount()
	{
		return this.definedBlocks;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.vector, this.size, this.definedBlocks);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof StructureBlueprint))
			return false;
		StructureBlueprint other = (StructureBlueprint)obj;

		if(!Objects.equals(this.size, other.size))
			return false;

		if(this.definedBlocks != other.definedBlocks)
			return false;

		return Objects.equals(this.vector, other.vector);
	}

	@Nonnull
	@Override
	public BlueprintReader reader()
	{
		return new RegionReader();
	}

	@Override
	public Set<? extends BlueprintEntry> palette()
	{
		return this.palette;
	}

	public BlueprintEditor edit()
	{
		BlueprintEditor blueprintEditor = new BlueprintEditor();
		blueprintEditor.importBlueprint(this);
		blueprintEditor.setDataVersion(this.version);
		return blueprintEditor;
	}

	public static BlueprintEditor begin()
	{
		return new BlueprintEditor();
	}

	private class RegionReader implements BlueprintReader
	{
		private final MutableBlueprintVoxel voxel;
		private int index;

		public RegionReader()
		{
			this.voxel = new MutableBlueprintVoxel();
			this.index = -1;
		}

		@Override
		public boolean hasNext()
		{
			return (this.index + 1) < StructureBlueprint.this.indexer.getVolume();
		}

		@Override
		public BlueprintVoxel next()
		{
			++this.index;

			BlockPos pos = StructureBlueprint.this.indexer.fromIndex(this.index).add(StructureBlueprint.this.getOriginOffset());
			this.voxel.set(pos, StructureBlueprint.this.vector.get(this.index));
			return this.voxel;
		}

		@Override
		public NBTTagCompound saveReaderState()
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("index", this.index);
			return tag;
		}

		@Override
		public void restoreReaderState(NBTTagCompound tag)
		{
			this.index = tag.getInteger("index");
		}
	}
}
