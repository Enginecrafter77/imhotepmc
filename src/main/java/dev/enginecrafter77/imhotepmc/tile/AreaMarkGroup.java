package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.entity.EntityConstructionTape;
import dev.enginecrafter77.imhotepmc.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class AreaMarkGroup implements INBTSerializable<NBTTagCompound> {
	private final BlockPos[] definingCorners;
	private final List<UUID> tapeEntities;
	private int defined;

	private AreaMarkGroup()
	{
		this.definingCorners = new BlockPos[4];
		this.defined = 0;
		this.tapeEntities = new ArrayList<UUID>(12);
	}

	public Set<Axis3d> getDefinedAxes()
	{
		if(this.defined <= 1)
			return EnumSet.noneOf(Axis3d.class);

		Set<Axis3d> set = EnumSet.noneOf(Axis3d.class);
		for(CombiningIterator.Pair<BlockPos, BlockPos> edge : CombiningIterator.selfCombinations(this.getDefinedCorners()))
		{
			Axis3d sharedAxis = BlockPosUtil.getSharedAxis(edge.getFirst(), edge.getSecond());
			if(sharedAxis == null)
				continue;
			set.add(sharedAxis);
		}
		return set;
	}

	@Nullable
	public AreaMarkGroup merge(AreaMarkGroup other)
	{
		if(other.defined > this.defined)
			return other.merge(this);

		if(other.defined != 1)
			return null;

		return this.expand(other.definingCorners[0]);
	}

	public int getDefined()
	{
		return this.defined;
	}

	private AreaMarkGroup deriveNext(BlockPos next)
	{
		if(this.defined == 4)
			throw new UnsupportedOperationException("Group already fully defined");

		AreaMarkGroup group = new AreaMarkGroup();
		System.arraycopy(this.definingCorners, 0, group.definingCorners, 0, this.defined);
		group.definingCorners[this.defined] = next;
		group.defined = this.defined + 1;
		return group;
	}

	@Nullable
	public AreaMarkGroup expand(BlockPos other)
	{
		Set<Axis3d> definedAxes = this.getDefinedAxes();

		for(CombiningIterator.Pair<BlockPos, BlockPos> edge : CombiningIterator.combinations(this.getDefinedCorners(), Collections.singletonList(other)))
		{
			Axis3d shared = BlockPosUtil.getSharedAxis(edge.getFirst(), edge.getSecond());
			if(shared == null || definedAxes.contains(shared))
				continue;
			return this.deriveNext(other);
		}
		return null;
	}

	public boolean isComplete()
	{
		return this.defined == 4;
	}

	public List<BlockPos> getDefinedCorners()
	{
		return ArrayWrapperList.of(this.definingCorners, 0, this.defined);
	}

	public void select(BlockSelectionBox box)
	{
		if(!this.isComplete())
			return;

		BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos();
		BlockPosUtil.findBoxMinMax(this.getDefinedCorners(), min, max);
		box.setStart(min);
		box.setEnd(max);
	}

	public List<BlockPosEdge> edges()
	{
		List<BlockPos> corners = BlockPosUtil.findCorners(this.getDefinedCorners()).distinct().collect(Collectors.toList());
		List<BlockPosEdge> edges = new ArrayList<BlockPosEdge>(12);
		for(int ti = 0; ti < corners.size(); ++ti)
		{
			BlockPos tbp = corners.get(ti);
			for(int ii = ti + 1; ii < corners.size(); ++ii)
			{
				BlockPos ibp = corners.get(ii);
				BlockPosEdge edge = BlockPosEdge.tryConnect(tbp, ibp);
				if(edge == null)
					continue;
				if(edge.getLength() == 0)
					continue;
				edges.add(edge);
			}
		}
		return edges;
	}

	public void dismantle(World world, IMarkerAccessor accessor)
	{
		for(BlockPos dc : this.getDefinedCorners())
		{
			IAreaMarker marker = accessor.getMarker(world, dc);
			if(marker == null)
				throw new IllegalStateException("Marker tile entity is null");
			marker.setMarkGroup(AreaMarkGroup.voxel(dc));
		}

		List<EntityConstructionTape> tapeEdges = world.getEntities(EntityConstructionTape.class, (EntityConstructionTape e) -> this.tapeEntities.contains(e.getUniqueID()));
		for(EntityConstructionTape tape : tapeEdges)
			tape.destroy();
	}

	public int getUsedTapeCount()
	{
		int count = 0;
		for(BlockPosEdge edge : this.edges())
		{
			Vec3d anchor1 = this.tapeAnchorFor(edge.getFirst());
			Vec3d anchor2 = this.tapeAnchorFor(edge.getSecond());
			double dist = anchor1.distanceTo(anchor2);
			count += EntityConstructionTape.getTapeItemsForLength(dist);
		}
		return count;
	}

	public void construct(World world, IMarkerAccessor accessor)
	{
		for(BlockPos dc : this.getDefinedCorners())
		{
			IAreaMarker marker = accessor.getMarker(world, dc);
			if(marker == null)
				throw new IllegalStateException("Marker tile entity is null");
			marker.setMarkGroup(this);
		}
		this.createTapeEntities(world);
	}

	protected void createTapeEntities(World world)
	{
		for(BlockPosEdge edge : this.edges())
		{
			Vec3d anchor1 = this.tapeAnchorFor(edge.getFirst());
			Vec3d anchor2 = this.tapeAnchorFor(edge.getSecond());
			EntityConstructionTape tape = new EntityConstructionTape(world);
			tape.setAnchor(anchor1, anchor2);
			world.spawnEntity(tape);
			this.tapeEntities.add(tape.getUniqueID());
		}
	}

	protected Vec3d tapeAnchorFor(BlockPos pos)
	{
		return new Vec3d(pos).add(0.5, 0.5, 0.5);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList tapes = new NBTTagList();
		for(UUID tape : this.tapeEntities)
			tapes.appendTag(NBTUtil.createUUIDTag(tape));
		tag.setTag("tape", tapes);
		NBTTagList list = new NBTTagList();
		for(int index = 0; index < this.defined; ++index)
			list.appendTag(NBTUtil.createPosTag(this.definingCorners[index]));
		tag.setTag("corners", list);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.tapeEntities.clear();
		NBTTagList tapeList = nbt.getTagList("tape", 10);
		for(int index = 0; index < tapeList.tagCount(); ++index)
			this.tapeEntities.add(NBTUtil.getUUIDFromTag(tapeList.getCompoundTagAt(index)));

		NBTTagList list = nbt.getTagList("corners", 10);
		for(this.defined = 0; this.defined < list.tagCount(); ++this.defined)
			this.definingCorners[this.defined] = NBTUtil.getPosFromTag(list.getCompoundTagAt(this.defined));
	}

	public static AreaMarkGroup voxel(BlockPos pos)
	{
		AreaMarkGroup group = new AreaMarkGroup();
		group.definingCorners[0] = pos;
		group.defined = 1;
		return group;
	}
}
