package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SchematicBlueprint implements Blueprint {
	private static final Vec3i ONE = new Vec3i(1, 1, 1);

	private final Map<String, OffsetRegionBlueprint> regions;
	private Instant modifyTime;
	private Instant createTime;
	private String description;
	private String author;
	private Vec3i size;

	public SchematicBlueprint()
	{
		this.regions = new HashMap<String, OffsetRegionBlueprint>();
		this.size = Vec3i.NULL_VECTOR;
		this.author = "Unknown";
		this.description = "";
		this.createTime = Instant.now();
		this.modifyTime = Instant.now();
	}

	public String getAuthor()
	{
		return this.author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public Instant getCreateTime()
	{
		return this.createTime;
	}

	public void setCreateTime(Instant createTime)
	{
		this.createTime = createTime;
	}

	public Instant getModifyTime()
	{
		return this.modifyTime;
	}

	public void setModifyTime(Instant instant)
	{
		this.modifyTime = instant;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void addRegion(String name, RegionBlueprint blueprint, BlockPos offset)
	{
		OffsetRegionBlueprint offsetBlueprint = new OffsetRegionBlueprint(blueprint, offset);
		BlockSelectionBox newRegionBox = new BlockSelectionBox();
		this.computeBlueprintBoundingBox(offsetBlueprint, newRegionBox);

		BlockSelectionBox totalBox = new BlockSelectionBox();

		BlockSelectionBox regionBox = new BlockSelectionBox();
		for(OffsetRegionBlueprint region : this.regions.values())
		{
			this.computeBlueprintBoundingBox(region, regionBox);
			regionBox.interect(newRegionBox);
			if(regionBox.getVolume() > 0)
				throw new IllegalArgumentException("Regions cannot overlap!");
			totalBox.union(regionBox);
		}

		this.regions.put(name, offsetBlueprint);
		this.size = totalBox.getSize();
	}

	public Iterable<String> getRegions()
	{
		return this.regions.keySet();
	}

	public int getRegionCount()
	{
		return this.regions.size();
	}

	@Override
	public int getBlockCount()
	{
		return this.regions.values().stream().mapToInt(Blueprint::getBlockCount).sum();
	}

	private void computeBlueprintBoundingBox(Blueprint blueprint, BlockSelectionBox box)
	{
		box.setStart(blueprint.getOrigin());
		box.setStart(blueprint.getOrigin().add(blueprint.getSize()).subtract(ONE));
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
	public ResolvedBlueprintBlock getBlockAt(BlockPos position)
	{
		BlockSelectionBox box = new BlockSelectionBox();
		for(OffsetRegionBlueprint blueprint : this.regions.values())
		{
			this.computeBlueprintBoundingBox(blueprint, box);
			if(box.contains(position))
				return blueprint.getBlockAt(position);
		}
		return null;
	}

	@Override
	public Vec3i getSize()
	{
		return this.size;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.author, this.createTime, this.modifyTime, this.description, this.size, this.regions);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SchematicBlueprint))
			return false;
		SchematicBlueprint other = (SchematicBlueprint)obj;
		boolean e1 = Objects.equals(this.author, other.author);
		boolean e2 = Objects.equals(this.createTime, other.createTime);
		boolean e3 = Objects.equals(this.modifyTime, other.modifyTime);
		boolean e4 = Objects.equals(this.description, other.description);
		boolean e5 = Objects.equals(this.size, other.size);
		boolean e6 = Objects.equals(this.regions, other.regions);
		return e1 && e2 && e3 && e4 && e5 && e6;
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

		@Override
		public BlockPos getOrigin()
		{
			return this.offset;
		}

		@Nullable
		@Override
		public ResolvedBlueprintBlock getBlockAt(BlockPos position)
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
