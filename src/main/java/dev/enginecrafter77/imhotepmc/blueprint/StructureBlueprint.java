package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StructureBlueprint {
	private static final StructureBlueprint EMPTY = new StructureBlueprint(ImmutableMap.of(), Vec3i.NULL_VECTOR);

	private final Map<Vec3i, ResolvedBlueprintBlock> blocks;
	private final Vec3i size;

	public StructureBlueprint(Map<Vec3i, ResolvedBlueprintBlock> blocks, Vec3i size)
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

	public StructureBlueprint translate(BlockRecordMapper mapper)
	{
		return this.edit().translate(mapper).build();
	}

	public StructureBlueprint.Builder edit()
	{
		StructureBlueprint.Builder builder = new StructureBlueprint.Builder();
		builder.merge(this);
		return builder;
	}

	public int getTotalBlocks()
	{
		return this.blocks.size();
	}

	public int getTotalVolume()
	{
		return this.size.getX() * this.size.getY() * this.size.getZ();
	}

	public Map<Vec3i, ResolvedBlueprintBlock> getStructureBlocks()
	{
		return this.blocks;
	}

	public static StructureBlueprint empty()
	{
		return EMPTY;
	}

	public static Builder builder()
	{
		return EMPTY.edit();
	}

	public static class Builder
	{
		private final Map<Vec3i, SavedTileState> data;

		public Builder()
		{
			this.data = new HashMap<Vec3i, SavedTileState>();
		}

		public void merge(StructureBlueprint other)
		{
			for(Map.Entry<Vec3i, ResolvedBlueprintBlock> entry : other.getStructureBlocks().entrySet())
				this.data.put(entry.getKey(), entry.getValue().save());
		}

		public StructureBlueprint.Builder addBlock(BlockPos position, SavedBlockState data)
		{
			return this.addBlock(position, new SavedTileState(data, null));
		}

		public StructureBlueprint.Builder addBlock(BlockPos position, SavedTileState data)
		{
			this.data.put(position, data);
			return this;
		}

		public StructureBlueprint.Builder addTileEntity(BlockPos position, NBTTagCompound tileEntityData)
		{
			SavedTileState state = this.data.get(position);
			if(state == null)
				return this;
			state = state.withTileEntity(tileEntityData);
			this.data.put(position, state);
			return this;
		}

		public StructureBlueprint.Builder translate(BlockRecordMapper mapper)
		{
			for(Vec3i key : this.data.keySet())
			{
				SavedTileState currentState = this.data.get(key);
				SavedTileState translated = mapper.translate(currentState);
				if(translated == null)
					this.data.remove(key);
				else
					this.data.put(key, translated);
			}
			return this;
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
			
			ImmutableMap.Builder<Vec3i, ResolvedBlueprintBlock> mb = ImmutableMap.builder();
			for(Map.Entry<Vec3i, SavedTileState> entry : this.data.entrySet())
			{
				Vec3i offset = VecUtil.difference(entry.getKey(), origin);
				ResolvedBlueprintBlock blueprintBlock = ResolvedBlueprintBlock.from(entry.getValue());
				mb.put(offset, blueprintBlock);
			}

			return new StructureBlueprint(mb.build(), size);
		}
	}
}
