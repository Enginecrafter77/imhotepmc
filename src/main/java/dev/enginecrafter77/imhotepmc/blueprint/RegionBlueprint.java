package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Map;
import java.util.Objects;

@Immutable
public class RegionBlueprint implements Blueprint {
	private final Map<BlockPos, SavedTileState> blocks;
	private final Vec3i size;

	public RegionBlueprint(Map<BlockPos, SavedTileState> blocks, Vec3i size)
	{
		this.blocks = blocks;
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
	public SavedTileState getBlockAt(BlockPos position)
	{
		return this.blocks.get(position);
	}

	@Override
	public int getDefinedBlockCount()
	{
		return this.blocks.size();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.blocks, this.size);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof RegionBlueprint))
			return false;
		RegionBlueprint other = (RegionBlueprint)obj;

		if(!Objects.equals(this.size, other.size))
			return false;

		return Objects.equals(this.blocks, other.blocks);
	}

	@Nonnull
	@Override
	public BlueprintReader reader()
	{
		return new RegionReader();
	}

	public BlueprintEditor edit()
	{
		BlueprintEditor blueprintEditor = new BlueprintEditor();
		blueprintEditor.importBlueprint(this);
		return blueprintEditor;
	}

	public int getTotalBlocks()
	{
		return this.blocks.size();
	}

	public Map<BlockPos, SavedTileState> getStructureBlocks()
	{
		return this.blocks;
	}

	public static BlueprintEditor begin()
	{
		return new BlueprintEditor();
	}

	private class RegionReader implements BlueprintReader
	{
		private final BlockPos.MutableBlockPos blockPos;
		private final MutableBlueprintVoxel voxel;
		private final VoxelIndexer indexer;
		private int index;

		public RegionReader()
		{
			this.blockPos = new BlockPos.MutableBlockPos();
			this.indexer = new NaturalVoxelIndexer(RegionBlueprint.this.getSize());
			this.voxel = new MutableBlueprintVoxel();
			this.index = -1;
		}

		private int findNextNonEmpty()
		{
			int next = this.index + 1;
			while(next < this.indexer.getVolume())
			{
				BlockPos pos = this.indexer.fromIndex(next);
				if(RegionBlueprint.this.blocks.containsKey(pos))
					break;
				++next;
			}
			return next;
		}

		@Override
		public boolean hasNext()
		{
			return this.findNextNonEmpty() < this.indexer.getVolume();
		}

		@Override
		public BlueprintVoxel next()
		{
			this.index = this.findNextNonEmpty();
			this.blockPos.setPos(this.indexer.fromIndex(this.index)).add(RegionBlueprint.this.getOriginOffset());
			this.voxel.set(this.blockPos, RegionBlueprint.this.getBlockAt(this.blockPos));
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
