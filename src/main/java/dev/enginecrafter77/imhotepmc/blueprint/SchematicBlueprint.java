package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.blueprint.iter.BlueprintVoxel;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import dev.enginecrafter77.imhotepmc.util.UnpackingIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class SchematicBlueprint extends SchematicMetadataWrapper implements Blueprint {
	private static final Vec3i ONE = new Vec3i(1, 1, 1);

	private final Map<String, OffsetRegionBlueprint> regions;
	private final SchematicMetadata metadata;

	protected SchematicBlueprint(SchematicMetadata metadata, Map<String, OffsetRegionBlueprint> regions)
	{
		this.metadata = metadata;
		this.regions = regions;
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
	public OffsetRegionBlueprint getRegion(String name)
	{
		OffsetRegionBlueprint blueprint = this.regions.get(name);
		if(blueprint == null)
			throw new NoSuchElementException();
		return blueprint;
	}

	@Nullable
	@Override
	public BlueprintEntry getBlockAt(BlockPos position)
	{
		BlockSelectionBox box = new BlockSelectionBox();
		for(OffsetRegionBlueprint blueprint : this.regions.values())
		{
			blueprint.computeBoundingBox(box);
			if(box.contains(position))
				return blueprint.getBlockAt(position);
		}
		return null;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.metadata, this.regions);
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
	public Iterator<BlueprintVoxel> iterator()
	{
		return new UnpackingIterator<Blueprint, BlueprintVoxel>(this.regions.values());
	}

	public SchematicBlueprint.Builder edit()
	{
		return new Builder(this);
	}

	public static SchematicBlueprint.Builder builder()
	{
		return new Builder();
	}

	public static class Builder
	{
		private final Map<String, OffsetRegionBlueprint> regions;
		private final MutableSchematicMetadata metadata;

		private int blockCount;
		private Vec3i size;

		public Builder()
		{
			this.regions = new HashMap<String, OffsetRegionBlueprint>();
			this.metadata = new MutableSchematicMetadata();
			this.size = Vec3i.NULL_VECTOR;
			this.blockCount = 0;
		}

		public Builder(SchematicBlueprint src)
		{
			this();
			this.regions.putAll(src.regions);
			this.metadata.set(src);
		}

		public Builder setMetadata(SchematicMetadata metadata)
		{
			this.metadata.set(metadata);
			return this;
		}

		public Builder addRegion(String name, RegionBlueprint blueprint, BlockPos offset)
		{
			OffsetRegionBlueprint offsetBlueprint = new OffsetRegionBlueprint(blueprint, offset);
			BlockSelectionBox newRegionBox = new BlockSelectionBox();
			offsetBlueprint.computeBoundingBox(newRegionBox);

			BlockSelectionBox totalBox = new BlockSelectionBox();

			BlockSelectionBox regionBox = new BlockSelectionBox();
			for(OffsetRegionBlueprint region : this.regions.values())
			{
				region.computeBoundingBox(regionBox);
				regionBox.intersect(newRegionBox);
				if(regionBox.getVolume() > 0)
					throw new IllegalArgumentException("Regions cannot overlap!");
				totalBox.union(regionBox);
			}
			totalBox.union(newRegionBox);

			this.regions.put(name, offsetBlueprint);
			this.size = totalBox.getSize();
			this.blockCount += offsetBlueprint.getBlockCount();

			return this;
		}

		public Builder addRegionsFrom(SchematicBlueprint other)
		{
			for(String regionName : other.getRegions())
			{
				OffsetRegionBlueprint blueprint = other.getRegion(regionName);
				this.addRegion(regionName, blueprint.getRegionBlueprint(), blueprint.getOrigin());
			}
			return this;
		}

		public SchematicBlueprint build()
		{
			MutableSchematicMetadata meta = this.metadata.copy();
			meta.setSize(this.size);
			meta.setBlockCount(this.blockCount);
			meta.setRegionCount(this.regions.size());
			return new SchematicBlueprint(meta, ImmutableMap.copyOf(this.regions));
		}
	}

	public static class OffsetRegionBlueprint implements Blueprint
	{
		private final RegionBlueprint regionBlueprint;
		private final BlockPos offset;

		public OffsetRegionBlueprint(RegionBlueprint blueprint, BlockPos offset)
		{
			this.regionBlueprint = blueprint;
			this.offset = offset;
		}

		public RegionBlueprint getRegionBlueprint()
		{
			return this.regionBlueprint;
		}

		public void computeBoundingBox(BlockSelectionBox box)
		{
			box.setStart(this.getOrigin());
			box.setEnd(this.getOrigin().add(this.getSize()).subtract(ONE));
		}

		@Override
		public BlockPos getOrigin()
		{
			return this.offset;
		}

		@Nullable
		@Override
		public BlueprintEntry getBlockAt(BlockPos position)
		{
			return this.regionBlueprint.getBlockAt(position.subtract(this.offset));
		}

		@Override
		public int getBlockCount()
		{
			return this.regionBlueprint.getBlockCount();
		}

		@Override
		public Vec3i getSize()
		{
			return this.regionBlueprint.getSize();
		}

		@Override
		public int hashCode()
		{
			return this.regionBlueprint.hashCode() + 1;
		}

		@Nonnull
		@Override
		public Iterator<BlueprintVoxel> iterator()
		{
			return this.regionBlueprint.iterator();
		}

		@Override
		public boolean equals(Object obj)
		{
			if(!(obj instanceof OffsetRegionBlueprint))
				return false;
			OffsetRegionBlueprint other = (OffsetRegionBlueprint)obj;
			return Objects.equals(this.offset, other.offset) && Objects.equals(this.regionBlueprint, other.regionBlueprint);
		}
	}
}
