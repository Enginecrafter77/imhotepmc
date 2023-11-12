package dev.enginecrafter77.imhotepmc.blueprint.iter;

import dev.enginecrafter77.imhotepmc.blueprint.Blueprint;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Iterator;

public class BlueprintIterator implements Iterator<BlueprintVoxel> {
	private final MutableBlueprintVoxel voxel;
	private final Blueprint blueprint;
	private final Iterator<BlockPos.MutableBlockPos> itr;

	public BlueprintIterator(Blueprint blueprint)
	{
		this.voxel = new MutableBlueprintVoxel();
		this.blueprint = blueprint;

		BlockPos origin = blueprint.getOrigin();
		Vec3i size = blueprint.getSize();
		BlockPos last = new BlockPos(size.getX() - 1, size.getY() - 1, size.getZ() - 1);
		this.itr = BlockPos.getAllInBoxMutable(origin, last.add(origin)).iterator();
	}

	@Override
	public boolean hasNext()
	{
		return this.itr.hasNext();
	}

	@Override
	public BlueprintVoxel next()
	{
		BlockPos pos = this.itr.next();
		BlueprintEntry block = this.blueprint.getBlockAt(pos);
		this.voxel.set(pos, block);
		return this.voxel;
	}
}
