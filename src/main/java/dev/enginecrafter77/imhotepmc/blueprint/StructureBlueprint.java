package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class StructureBlueprint {
	private final Map<Vec3i, StructureBlockSavedData> blocks;
	private final Vec3i size;

	public StructureBlueprint(Map<Vec3i, StructureBlockSavedData> blocks, Vec3i size)
	{
		this.blocks = blocks;
		this.size = size;
	}

	public Vec3i getSize()
	{
		return this.size;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.blocks, this.size);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof StructureBlueprint))
			return false;
		StructureBlueprint other = (StructureBlueprint)obj;

		if(!Objects.equals(this.size, other.size))
			return false;

		return Objects.equals(this.blocks, other.blocks);
	}

	public int getTotalBlocks()
	{
		return this.blocks.size();
	}

	public int getTotalVolume()
	{
		return this.size.getX() * this.size.getY() * this.size.getZ();
	}

	public Map<Vec3i, StructureBlockSavedData> getStructureBlocks()
	{
		return this.blocks;
	}

	public Iterator<StructureBlockSavedData> getOrderedIterator()
	{
		return this.getStructureBlocks().entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).iterator();
	}

	private static final StructureBlueprint EMPTY = new StructureBlueprint(ImmutableMap.of(), Vec3i.NULL_VECTOR);
	public static StructureBlueprint empty()
	{
		return EMPTY;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder
	{
		private final Map<Vec3i, StructureBlockSavedData> data;

		public Builder()
		{
			this.data = new HashMap<Vec3i, StructureBlockSavedData>();
		}

		public void addBlock(BlockPos position, StructureBlockSavedData data)
		{
			this.data.put(position, data);
		}

		public StructureBlueprint build()
		{
			if(this.data.isEmpty())
				return StructureBlueprint.empty();

			int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE, minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
			for(Vec3i pos : this.data.keySet())
			{
				if(pos.getX() < minX)
					minX = pos.getX();
				if(pos.getX() > maxX)
					maxX = pos.getX();
				if(pos.getY() < minY)
					minY = pos.getY();
				if(pos.getY() > maxY)
					maxY = pos.getY();
				if(pos.getZ() < minZ)
					minZ = pos.getZ();
				if(pos.getZ() > maxZ)
					maxZ = pos.getZ();
			}
			Vec3i size = new Vec3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
			Vec3i origin = new Vec3i(minX, minY, minZ);
			
			ImmutableMap.Builder<Vec3i, StructureBlockSavedData> mb = ImmutableMap.builder();
			for(Map.Entry<Vec3i, StructureBlockSavedData> entry : this.data.entrySet())
			{
				Vec3i offset = VecUtil.difference(entry.getKey(), origin);
				mb.put(offset, entry.getValue());
			}

			return new StructureBlueprint(mb.build(), size);
		}
	}
}
