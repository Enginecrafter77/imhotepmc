package dev.enginecrafter77.imhotepmc.marker;

import dev.enginecrafter77.imhotepmc.util.Axis3d;
import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import dev.enginecrafter77.imhotepmc.util.VecUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import dev.enginecrafter77.imhotepmc.util.math.Edge3i;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

public class AreaMarkBox {
	private final List<BlockPos> definingCorners;

	/** A box enclosing the marked area. Computed in {@link #updateBox()} */
	private final Box3i box;

	/** An enum of the axes of all edge-aligned connections between corners. Computed in {@link #updateBox()}. */
	private final EnumSet<Axis3d> edgeAxes;

	public AreaMarkBox()
	{
		this.edgeAxes = EnumSet.noneOf(Axis3d.class);
		this.box = new Box3i();
		this.definingCorners = new ArrayList<>(4);
	}

	public void set(AreaMarkBox other)
	{
		this.definingCorners.clear();
		this.edgeAxes.clear();

		this.definingCorners.addAll(other.definingCorners);
		this.edgeAxes.addAll(other.edgeAxes);
		this.box.set(other.box);
	}

	public Box3i getBox()
	{
		return this.box;
	}

	public int getDefined()
	{
		return this.definingCorners.size();
	}

	public EnumSet<Axis3d> getDefinedAxes()
	{
		return this.edgeAxes;
	}

	public boolean isFullyDefined()
	{
		return this.definingCorners.size() == 4;
	}

	public boolean isEmpty()
	{
		return this.definingCorners.isEmpty();
	}

	@Nullable
	private Axis3d getAxisConnectingToDefiningCorner(BlockPos newBlock)
	{
		for(BlockPos corner : this.definingCorners)
		{
			Axis3d axis = BlockPosUtil.getSharedAxis(corner, newBlock);
			if(axis != null)
				return axis;
		}
		return null;
	}

	public AreaExpandResult evaluateNewMember(BlockPos pos)
	{
		if(this.isEmpty())
			return AreaExpandResult.SUCCESS;
		Axis3d axis = this.getAxisConnectingToDefiningCorner(pos);
		if(axis == null)
			return AreaExpandResult.NO_CONNECTING_AXIS;
		if(this.edgeAxes.contains(axis))
			return AreaExpandResult.NO_EXPANDING_AXIS;
		return AreaExpandResult.SUCCESS;
	}

	public void add(BlockPos other)
	{
		assert this.evaluateNewMember(other) == AreaExpandResult.SUCCESS;
		this.definingCorners.add(other);
		this.updateBox();
	}

	public void remove(BlockPos other)
	{
		this.definingCorners.remove(other);
		this.updateBox();
	}

	private void updateBox()
	{
		VecUtil.boxCoveringBlocks(this.definingCorners, this.box);

		this.edgeAxes.clear();
		if(this.box.getSizeX() > 1)
			this.edgeAxes.add(Axis3d.X);
		if(this.box.getSizeY() > 1)
			this.edgeAxes.add(Axis3d.Y);
		if(this.box.getSizeZ() > 1)
			this.edgeAxes.add(Axis3d.Z);
	}

	public List<BlockPos> getDefiningCorners()
	{
		return Collections.unmodifiableList(this.definingCorners);
	}

	public int getTotalEdgeLength()
	{
		return this.getBox()
				.edges()
				.stream()
				.mapToInt(Edge3i::length)
				.sum();
	}
}
