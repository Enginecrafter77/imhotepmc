package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.blueprint.iter.BlueprintVoxel;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import dev.enginecrafter77.imhotepmc.util.UnpackingIterator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

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
	public BlueprintBuilder schematicBuilder()
	{
		return new SchematicBlueprintBuilder();
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
		public BlueprintBuilder schematicBuilder()
		{
			return this.regionBlueprint.schematicBuilder();
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

	private class SchematicBlueprintBuilder implements BlueprintBuilder
	{
		private final Map<String, BlueprintBuilder> regionBuilders;
		private final List<String> regionOrder;
		private int regionIndex;

		public SchematicBlueprintBuilder()
		{
			this.regionOrder = new ArrayList<String>(SchematicBlueprint.this.regions.keySet());
			this.regionBuilders = new HashMap<String, BlueprintBuilder>();
			this.regionIndex = -1;

			for(String region : this.regionOrder)
				this.regionBuilders.put(region, SchematicBlueprint.this.getRegion(region).schematicBuilder());
		}

		private BlueprintBuilder getBuilderAt(int region)
		{
			return this.regionBuilders.get(this.regionOrder.get(region));
		}

		private int findAvailableBuilder()
		{
			if(this.regionIndex >= 0 && this.getBuilderAt(this.regionIndex).hasNextBlock())
				return this.regionIndex;

			int next = this.regionIndex + 1;
			while(next < this.regionOrder.size() && !this.getBuilderAt(next).hasNextBlock())
				++next;
			return next;
		}

		@Override
		public boolean hasNextBlock()
		{
			return this.findAvailableBuilder() < this.regionOrder.size();
		}

		@Override
		public void placeNextBlock(World world, BlockPos origin)
		{
			this.regionIndex = this.findAvailableBuilder();
			this.getBuilderAt(this.regionIndex).placeNextBlock(world, origin);
		}

		@Override
		public NBTTagCompound saveState()
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList regionOrder = new NBTTagList();
			NBTTagCompound builders = new NBTTagCompound();
			for(String reg : this.regionOrder)
			{
				regionOrder.appendTag(new NBTTagString(reg));
				builders.setTag(reg, this.regionBuilders.get(reg).saveState());
			}
			tag.setTag("region_order", regionOrder);
			tag.setTag("builders", builders);
			tag.setInteger("region", this.regionIndex);
			return tag;
		}

		@Override
		public void restoreState(NBTTagCompound tag)
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
				this.regionBuilders.get(reg).restoreState(builderState);
			}
		}
	}
}
