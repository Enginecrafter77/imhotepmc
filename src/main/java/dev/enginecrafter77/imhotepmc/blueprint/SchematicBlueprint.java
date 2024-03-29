package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableSet;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import java.util.*;

public class SchematicBlueprint extends SchematicMetadataWrapper implements Blueprint {
	protected final Map<String, SchematicRegionBlueprint> regions;
	private final Set<? extends BlueprintEntry> palette;
	private final SchematicMetadata metadata;
	private final int dataVersion;

	protected SchematicBlueprint(SchematicMetadata metadata, Map<String, SchematicRegionBlueprint> regions, int dataVersion)
	{
		this.palette = compilePalette(regions.values());
		this.metadata = metadata;
		this.regions = regions;
		this.dataVersion = dataVersion;
	}

	@Override
	public int getDataVersion()
	{
		return this.dataVersion;
	}

	@Override
	protected SchematicMetadata getWrappedMetadata()
	{
		return this.metadata;
	}

	public Iterable<String> getRegions()
	{
		return this.regions.keySet();
	}

	@Nonnull
	public SchematicRegionBlueprint getRegion(String name)
	{
		SchematicRegionBlueprint blueprint = this.regions.get(name);
		if(blueprint == null)
			throw new NoSuchElementException();
		return blueprint;
	}

	@Override
	public BlockPos getOriginOffset()
	{
		return BlockPos.ORIGIN;
	}

	@Override
	public BlueprintEntry getBlockAt(BlockPos position)
	{
		BlockSelectionBox box = new BlockSelectionBox();
		for(SchematicRegionBlueprint blueprint : this.regions.values())
		{
			blueprint.computeBoundingBox(box);
			if(box.contains(position))
				return blueprint.getBlockAt(position);
		}
		return SavedTileState.air();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.metadata, this.regions);
	}

	@Override
	public int getDefinedBlockCount()
	{
		return this.regions.values().parallelStream().mapToInt(Blueprint::getDefinedBlockCount).sum();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SchematicBlueprint))
			return false;
		SchematicBlueprint other = (SchematicBlueprint)obj;
		return Objects.equals(this.metadata, other.metadata) && Objects.equals(this.regions, other.regions);
	}

	@Nonnull
	@Override
	public BlueprintReader reader()
	{
		return new SchematicBlueprintReader();
	}

	@Override
	public Set<? extends BlueprintEntry> palette()
	{
		return this.palette;
	}

	public SchematicEditor edit()
	{
		return new SchematicEditor(this);
	}

	public static SchematicEditor builder()
	{
		return new SchematicEditor();
	}

	private static Set<BlueprintEntry> compilePalette(Iterable<? extends Blueprint> blueprints)
	{
		ImmutableSet.Builder<BlueprintEntry> sb = ImmutableSet.builder();
		for(Blueprint blueprint : blueprints)
			sb.addAll(blueprint.palette());
		return sb.build();
	}

	public static class SchematicRegionBlueprint implements Blueprint
	{
		private final StructureBlueprint structureBlueprint;
		private final BlockPos offset;

		public SchematicRegionBlueprint(StructureBlueprint blueprint, BlockPos offset)
		{
			this.structureBlueprint = blueprint;
			this.offset = offset;
		}

		public StructureBlueprint getRegionBlueprint()
		{
			return this.structureBlueprint;
		}

		public void computeBoundingBox(BlockSelectionBox box)
		{
			box.setStartSize(this.getOriginOffset(), this.getSize());
		}

		@Override
		public BlockPos getOriginOffset()
		{
			return this.offset;
		}

		@Override
		public BlueprintEntry getBlockAt(BlockPos position)
		{
			return this.structureBlueprint.getBlockAt(position.subtract(this.offset));
		}

		@Override
		public int getDefinedBlockCount()
		{
			return this.structureBlueprint.getDefinedBlockCount();
		}

		@Override
		public Vec3i getSize()
		{
			return this.structureBlueprint.getSize();
		}

		@Nonnull
		@Override
		public BlueprintReader reader()
		{
			return this.structureBlueprint.reader();
		}

		@Override
		public int getDataVersion()
		{
			return this.structureBlueprint.getDataVersion();
		}

		@Override
		public Set<? extends BlueprintEntry> palette()
		{
			return this.structureBlueprint.palette();
		}

		@Override
		public int hashCode()
		{
			return this.structureBlueprint.hashCode() + 1;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(!(obj instanceof SchematicRegionBlueprint))
				return false;
			SchematicRegionBlueprint other = (SchematicRegionBlueprint)obj;
			return Objects.equals(this.offset, other.offset) && Objects.equals(this.structureBlueprint, other.structureBlueprint);
		}
	}

	private class SchematicBlueprintReader implements BlueprintReader
	{
		private final Map<String, BlueprintReader> regionReaders;
		private final List<String> regionOrder;
		private int regionIndex;

		public SchematicBlueprintReader()
		{
			this.regionOrder = new ArrayList<String>(SchematicBlueprint.this.regions.keySet());
			this.regionReaders = new HashMap<String, BlueprintReader>();
			this.regionIndex = -1;

			for(String region : this.regionOrder)
				this.regionReaders.put(region, SchematicBlueprint.this.getRegion(region).reader());
		}

		private BlueprintReader getReaderAt(int region)
		{
			return this.regionReaders.get(this.regionOrder.get(region));
		}

		private int findAvailableBuilder()
		{
			if(this.regionIndex >= 0 && this.getReaderAt(this.regionIndex).hasNext())
				return this.regionIndex;

			int next = this.regionIndex + 1;
			while(next < this.regionOrder.size() && !this.getReaderAt(next).hasNext())
				++next;
			return next;
		}

		@Override
		public boolean hasNext()
		{
			return this.findAvailableBuilder() < this.regionOrder.size();
		}

		@Override
		public BlueprintVoxel next()
		{
			this.regionIndex = this.findAvailableBuilder();
			return this.getReaderAt(this.regionIndex).next();
		}

		@Override
		public NBTTagCompound saveReaderState()
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList regionOrder = new NBTTagList();
			NBTTagCompound builders = new NBTTagCompound();
			for(String reg : this.regionOrder)
			{
				regionOrder.appendTag(new NBTTagString(reg));
				builders.setTag(reg, this.regionReaders.get(reg).saveReaderState());
			}
			tag.setTag("region_order", regionOrder);
			tag.setTag("builders", builders);
			tag.setInteger("region", this.regionIndex);
			return tag;
		}

		@Override
		public void restoreReaderState(NBTTagCompound tag)
		{
			NBTTagList regionOrder = tag.getTagList("region_order", 8); // 8 => NBTTagString
			this.regionOrder.clear();
			for(int index = 0; index < regionOrder.tagCount(); ++index)
				this.regionOrder.add(regionOrder.getStringTagAt(index));
			this.regionIndex = tag.getInteger("region");

			NBTTagCompound builders = tag.getCompoundTag("builders");
			for(String reg : this.regionOrder)
			{
				NBTTagCompound builderState = builders.getCompoundTag(reg);
				this.regionReaders.get(reg).restoreReaderState(builderState);
			}
		}
	}
}
