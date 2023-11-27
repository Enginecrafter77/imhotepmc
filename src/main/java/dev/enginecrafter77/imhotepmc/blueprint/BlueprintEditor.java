package dev.enginecrafter77.imhotepmc.blueprint;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslation;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslationContext;
import dev.enginecrafter77.imhotepmc.blueprint.translate.CommonTranslationContext;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BlueprintEditor {
	private final Map<BlockPos, SavedTileState> data;

	@Nullable
	private Vec3i size;

	public BlueprintEditor()
	{
		this.data = new HashMap<BlockPos, SavedTileState>();
		this.size = null;
	}

	public void importBlueprint(Blueprint other)
	{
		other.reader().forEachRemaining(this::addVoxel);
	}

	public void addVoxel(BlueprintVoxel voxel)
	{
		this.data.put(voxel.getPosition().toImmutable(), SavedTileState.copyOf(voxel));
	}

	public BlueprintEditor setSize(Vec3i size)
	{
		this.size = size;
		return this;
	}

	public BlueprintEditor addBlock(BlockPos position, SavedBlockState data)
	{
		return this.addBlock(position, new SavedTileState(data, null));
	}

	public BlueprintEditor addBlock(BlockPos position, SavedTileState data)
	{
		this.data.put(position, data);
		return this;
	}

	public BlueprintEditor addTileEntity(BlockPos position, NBTTagCompound tileEntityData)
	{
		SavedTileState state = this.data.get(position);
		if(state == null)
			return this;
		state = state.withTileEntity(tileEntityData);
		this.data.put(position, state);
		return this;
	}

	public BlueprintEditor translate(BlueprintTranslation mapper)
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
			throw new UnsupportedOperationException("Cannot create empty region blueprint!");

		Vec3i size = this.size;
		Vec3i origin = Vec3i.NULL_VECTOR;

		if(size == null)
		{
			BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos();
			BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos();
			BlockPosUtil.findBoxMinMax(this.data.keySet(), min, max);
			size = max.subtract(min).add(1, 1, 1);
			origin = min.toImmutable();
		}

		ImmutableMap.Builder<BlockPos, SavedTileState> mb = ImmutableMap.builder();
		for(Map.Entry<BlockPos, SavedTileState> entry : this.data.entrySet())
		{
			BlockPos offset = new BlockPos(VecUtil.difference(entry.getKey(), origin));
			mb.put(offset, entry.getValue());
		}
		return new RegionBlueprint(mb.build(), size);
	}
}
