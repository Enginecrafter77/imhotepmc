package dev.enginecrafter77.imhotepmc.util;

import dev.enginecrafter77.imhotepmc.blueprint.builder.ShapeGenerator;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;

public class ShapedBlockPosIterator implements Iterator<BlockPos.MutableBlockPos> {
	private final Iterator<BlockPos.MutableBlockPos> fullVolumeIterator;

	private final BlockSelectionBox box;
	private final ShapeGenerator generator;

	private final BlockPos.MutableBlockPos lastPosition;
	private final BlockPos.MutableBlockPos nextPosition;
	private boolean foundNext;

	public ShapedBlockPosIterator(BlockSelectionBox box, ShapeGenerator generator)
	{
		this.fullVolumeIterator = box.internalBlocks().iterator();
		this.box = box;
		this.generator = generator;
		this.lastPosition = new BlockPos.MutableBlockPos();
		this.nextPosition = new BlockPos.MutableBlockPos();
		this.foundNext = false;

		this.foundNext = this.findNextCandidate(); // Prepare the nextPosition for the next invocation of
	}

	protected boolean findNextCandidate()
	{
		this.lastPosition.setPos(this.nextPosition);

		while(this.fullVolumeIterator.hasNext())
		{
			BlockPos.MutableBlockPos pos = this.fullVolumeIterator.next();
			this.nextPosition.setPos(pos);
			if(this.generator.isBlockInShape(this.box, this.nextPosition))
				return true;
		}
		return false;
	}

	@Override
	public boolean hasNext()
	{
		return this.foundNext;
	}

	@Override
	public BlockPos.MutableBlockPos next()
	{
		this.foundNext = this.findNextCandidate();
		return this.lastPosition;
	}

	public static Iterable<BlockPos.MutableBlockPos> asIterable(BlockSelectionBox box, ShapeGenerator generator)
	{
		return () -> new ShapedBlockPosIterator(box, generator);
	}
}
