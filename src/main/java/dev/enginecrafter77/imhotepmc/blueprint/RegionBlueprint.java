package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.blueprint.iter.BlueprintVoxel;
import dev.enginecrafter77.imhotepmc.blueprint.iter.MutableBlueprintVoxel;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslation;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslationContext;
import dev.enginecrafter77.imhotepmc.blueprint.translate.CommonTranslationContext;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class RegionBlueprint implements Blueprint {
	private static final RegionBlueprint EMPTY = new RegionBlueprint(ImmutableMap.of(), Vec3i.NULL_VECTOR);

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

	@Nullable
	@Override
	public SavedTileState getBlockAt(BlockPos position)
	{
		return this.blocks.get(position);
	}

	@Override
	public int getBlockCount()
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
	public Iterator<BlueprintVoxel> iterator()
	{
		return new RegionIterator();
	}

	public RegionBlueprint translate(BlueprintTranslation mapper)
	{
		return this.edit().translate(mapper).build();
	}

	public RegionBlueprint.Builder edit()
	{
		RegionBlueprint.Builder builder = new RegionBlueprint.Builder();
		builder.merge(this);
		return builder;
	}

	public int getTotalBlocks()
	{
		return this.blocks.size();
	}

	public Map<BlockPos, SavedTileState> getStructureBlocks()
	{
		return this.blocks;
	}

	public static RegionBlueprint empty()
	{
		return EMPTY;
	}

	public static Builder builder()
	{
		return EMPTY.edit();
	}

	public static class Builder
	{
		private final Map<BlockPos, SavedTileState> data;

		public Builder()
		{
			this.data = new HashMap<BlockPos, SavedTileState>();
		}

		public void merge(RegionBlueprint other)
		{
			this.data.putAll(other.blocks);
		}

		public RegionBlueprint.Builder addBlock(BlockPos position, SavedBlockState data)
		{
			return this.addBlock(position, new SavedTileState(data, null));
		}

		public RegionBlueprint.Builder addBlock(BlockPos position, SavedTileState data)
		{
			this.data.put(position, data);
			return this;
		}

		public RegionBlueprint.Builder addTileEntity(BlockPos position, NBTTagCompound tileEntityData)
		{
			SavedTileState state = this.data.get(position);
			if(state == null)
				return this;
			state = state.withTileEntity(tileEntityData);
			this.data.put(position, state);
			return this;
		}

		public RegionBlueprint.Builder translate(BlueprintTranslation mapper)
		{
			BlueprintTranslationContext ctx = new CommonTranslationContext(mapper, this.data::get);

			for(BlockPos key : this.data.keySet())
			{
				SavedTileState currentState = this.data.get(key);
				SavedTileState translated = mapper.translate(ctx, key, currentState);
				if(translated == null)
					this.data.remove(key);
				else
					this.data.put(key, translated);
			}
			return this;
		}

		public RegionBlueprint build()
		{
			if(this.data.isEmpty())
				return RegionBlueprint.empty();

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
			
			ImmutableMap.Builder<BlockPos, SavedTileState> mb = ImmutableMap.builder();
			for(Map.Entry<BlockPos, SavedTileState> entry : this.data.entrySet())
			{
				BlockPos offset = new BlockPos(VecUtil.difference(entry.getKey(), origin));
				mb.put(offset, entry.getValue());
			}
			return new RegionBlueprint(mb.build(), size);
		}
	}

	private class RegionIterator implements Iterator<BlueprintVoxel>
	{
		private final Iterator<Map.Entry<BlockPos, SavedTileState>> iterator;
		private final MutableBlueprintVoxel voxel;

		public RegionIterator()
		{
			this.iterator = RegionBlueprint.this.blocks.entrySet().iterator();
			this.voxel = new MutableBlueprintVoxel();
		}

		@Override
		public boolean hasNext()
		{
			return this.iterator.hasNext();
		}

		@Override
		public BlueprintVoxel next()
		{
			Map.Entry<BlockPos, SavedTileState> entry = this.iterator.next();
			this.voxel.set(entry.getKey(), entry.getValue());
			return this.voxel;
		}
	}
}
