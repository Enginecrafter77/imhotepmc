package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Set;

@Immutable
public class RegionBlueprint implements Blueprint {
	private final Set<SavedTileState> palette;
	private final CompactPalettedBitVector<SavedTileState> vector;
	private final VoxelIndexer indexer;
	private final int definedBlocks;
	private final Vec3i size;

	protected RegionBlueprint(VoxelIndexer indexer, Set<SavedTileState> palette, CompactPalettedBitVector<SavedTileState> vector, Vec3i size, int definedBlocks)
	{
		this.definedBlocks = definedBlocks;
		this.indexer = indexer;
		this.palette = palette;
		this.vector = vector;
		this.size = size;
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

	@Nullable
	@Override
	public BlueprintEntry getBlockAt(BlockPos position)
	{
		position = position.subtract(this.getOriginOffset());
		int index = this.indexer.toIndex(position);
		BlueprintEntry entry = this.vector.get(index);
		if(entry.getBlockName().equals(Blocks.AIR.getRegistryName()))
			return null;
		return entry;
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
		if(!(obj instanceof RegionBlueprint))
			return false;
		RegionBlueprint other = (RegionBlueprint)obj;

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

		private int findNextNonEmpty()
		{
			int next = this.index + 1;
			while(next < RegionBlueprint.this.indexer.getVolume())
			{
				SavedTileState entry = RegionBlueprint.this.vector.get(next);
				if(!entry.getBlockName().equals(Blocks.AIR.getRegistryName()))
					break;
				++next;
			}
			return next;
		}

		@Override
		public boolean hasNext()
		{
			return this.findNextNonEmpty() < RegionBlueprint.this.indexer.getVolume();
		}

		@Override
		public BlueprintVoxel next()
		{
			this.index = this.findNextNonEmpty();
			BlockPos pos = RegionBlueprint.this.indexer.fromIndex(this.index).add(RegionBlueprint.this.getOriginOffset());
			this.voxel.set(pos, RegionBlueprint.this.vector.get(this.index));
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
