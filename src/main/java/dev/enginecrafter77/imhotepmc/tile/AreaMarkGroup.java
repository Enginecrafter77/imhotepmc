package dev.enginecrafter77.imhotepmc.tile;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.enginecrafter77.imhotepmc.util.*;
import dev.enginecrafter77.imhotepmc.world.AreaMarkDatabase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.*;

public class AreaMarkGroup implements INBTSerializable<NBTTagCompound> {
	private static float TAPE_ITEMS_PER_BLOCK = 0.125F;

	private static final String NBT_KEY_CORNERS = "corners";

	private final BlockPos[] definingCorners;
	private final UUID id;
	private int defined;

	@Nullable private ImmutableSet<Axis3d> definedAxes;
	@Nullable private ImmutableList<BlockPosEdge> blockEdges;
	@Nullable private ImmutableList<Edge3d> tapeEdges;
	@Nullable private AxisAlignedBB boundingBox;

	public AreaMarkGroup(UUID id)
	{
		this.definingCorners = new BlockPos[4];
		this.defined = 0;
		this.id = id;

		this.definedAxes = null;
		this.blockEdges = null;
		this.tapeEdges = null;
		this.boundingBox = null;
	}

	public AreaMarkGroup(UUID id, BlockPos[] corners)
	{
		this(id);
		if(corners.length > 4)
			throw new IllegalArgumentException();
		this.defined = corners.length;
		System.arraycopy(corners, 0, this.definingCorners, 0, corners.length);
		this.computeParameters();
	}

	private void computeParameters()
	{
		this.definedAxes = ImmutableSet.copyOf(calculateDefinedAxes(this.getDefiningCorners()));
		this.boundingBox = BlockPosUtil.contain(this.getDefiningCorners());
		this.blockEdges = ImmutableList.copyOf(BlockPosUtil.findEdges(this.getDefiningCorners()));
		this.tapeEdges = this.blockEdges.stream().map(this::transformBlockEdge).collect(ImmutableListCollector.get());
	}

	private Edge3d transformBlockEdge(BlockPosEdge edge)
	{
		Vec3d anchor1 = this.tapeAnchorFor(edge.getFirst());
		Vec3d anchor2 = this.tapeAnchorFor(edge.getSecond());
		Edge3d ne = new Edge3d();
		ne.set(anchor1, anchor2);
		return new Edge3d.ImmutableEdge3d(ne);
	}

	public UUID getId()
	{
		return this.id;
	}

	public AxisAlignedBB getBoundingBox()
	{
		if(this.boundingBox == null)
			throw new IllegalStateException();
		return this.boundingBox;
	}

	public Collection<BlockPosEdge> getBlockEdges()
	{
		if(this.blockEdges == null)
			throw new IllegalStateException();
		return Collections.unmodifiableList(this.blockEdges);
	}

	public Collection<Edge3d> getTapeEdges()
	{
		if(this.tapeEdges == null)
			throw new IllegalStateException();
		return Collections.unmodifiableList(this.tapeEdges);
	}

	public Set<Axis3d> getDefinedAxes()
	{
		if(this.definedAxes == null)
			throw new IllegalStateException();
		return Collections.unmodifiableSet(this.definedAxes);
	}

	public int getDefined()
	{
		return this.defined;
	}

	private AreaMarkGroup deriveNext(BlockPos next)
	{
		if(this.defined == 4)
			throw new UnsupportedOperationException("Group already fully defined");

		AreaMarkGroup group = new AreaMarkGroup(UUID.randomUUID());
		System.arraycopy(this.definingCorners, 0, group.definingCorners, 0, this.defined);
		group.definingCorners[this.defined] = next;
		group.defined = this.defined + 1;
		group.computeParameters();
		return group;
	}

	@Nullable
	public AreaMarkGroup expand(BlockPos other)
	{
		Set<Axis3d> definedAxes = this.getDefinedAxes();

		for(CombiningIterator.Pair<BlockPos, BlockPos> edge : CombiningIterator.combinations(this.getDefiningCorners(), Collections.singletonList(other)))
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

	public List<BlockPos> getDefiningCorners()
	{
		return ArrayWrapperList.of(this.definingCorners, 0, this.defined);
	}

	public void select(BlockSelectionBox box)
	{
		if(!this.isComplete())
			return;

		BlockPos.MutableBlockPos min = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos max = new BlockPos.MutableBlockPos();
		BlockPosUtil.findBoxMinMax(this.getDefiningCorners(), min, max);
		box.setStart(min);
		box.setEnd(max);
	}

	public void construct(World world)
	{
		AreaMarkDatabase db = AreaMarkDatabase.getDefault(world);
		if(db == null)
			throw new IllegalStateException();
		db.registerGroup(this);

		for(BlockPos dc : this.getDefiningCorners())
		{
			IAreaMarker marker = (IAreaMarker)world.getTileEntity(dc);
			if(marker == null)
				throw new IllegalStateException("Marker tile entity is null");
			marker.setMarkGroup(this);
		}
	}

	public void dismantle(World world)
	{
		AreaMarkDatabase db = AreaMarkDatabase.getDefault(world);
		if(db == null)
			throw new IllegalStateException();

		for(BlockPos dc : this.getDefiningCorners())
		{
			IAreaMarker marker = (IAreaMarker)world.getTileEntity(dc);
			if(marker == null)
				throw new IllegalStateException("Marker tile entity is null");
			marker.setMarkGroup(null);
		}

		db.unregisterGroup(this);
	}

	public int getUsedTapeCount()
	{
		double length = this.getTapeEdges().stream().mapToDouble(Edge3d::getLength).sum();
		return (int)Math.ceil(length * TAPE_ITEMS_PER_BLOCK);
	}

	protected Vec3d tapeAnchorFor(BlockPos pos)
	{
		return new Vec3d(pos).add(0.5, 0.5, 0.5);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for(int index = 0; index < this.defined; ++index)
			list.appendTag(NBTUtil.createPosTag(this.definingCorners[index]));
		tag.setTag(NBT_KEY_CORNERS, list);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		NBTTagList list = nbt.getTagList(NBT_KEY_CORNERS, 10);
		for(this.defined = 0; this.defined < list.tagCount(); ++this.defined)
			this.definingCorners[this.defined] = NBTUtil.getPosFromTag(list.getCompoundTagAt(this.defined));
		this.computeParameters();
	}

	@Nullable
	public static AreaMarkGroup create(BlockPos first, BlockPos second)
	{
		if(Objects.equals(first, second) || BlockPosUtil.getSharedAxis(first, second) == null)
			return null;
		return new AreaMarkGroup(UUID.randomUUID(), new BlockPos[] {first, second});
	}

	protected static Set<Axis3d> calculateDefinedAxes(List<BlockPos> definingCorners)
	{
		if(definingCorners.size() <= 1)
			return EnumSet.noneOf(Axis3d.class);

		Set<Axis3d> set = EnumSet.noneOf(Axis3d.class);
		for(CombiningIterator.Pair<BlockPos, BlockPos> edge : CombiningIterator.selfCombinations(definingCorners))
		{
			Axis3d sharedAxis = BlockPosUtil.getSharedAxis(edge.getFirst(), edge.getSecond());
			if(sharedAxis == null)
				continue;
			set.add(sharedAxis);
		}
		return set;
	}
}
