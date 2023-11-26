package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.entity.EntityConstructionTape;
import dev.enginecrafter77.imhotepmc.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class TileEntityAreaMarker extends TileEntity {
	private static final String NBT_KEY_LINK = "link";

	@Nonnull
	private AreaMarkGroup group;

	public TileEntityAreaMarker()
	{
		this.group = AreaMarkGroup.voxel(BlockPos.ORIGIN);
	}

	public AreaMarkGroup getMarkGroup()
	{
		return this.group;
	}

	public boolean tryConnect(TileEntityAreaMarker other)
	{
		AreaMarkGroup ng = this.group.expand(other.getPos());
		if(ng == null)
			return false;

		this.group.dismantle(this.world);
		ng.construct(this.world);
		return true;
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		if(this.group.defined == 1)
			this.group = AreaMarkGroup.voxel(this.getPos());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.group.deserializeNBT(compound.getCompoundTag(NBT_KEY_LINK));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag(NBT_KEY_LINK, this.group.serializeNBT());
		return compound;
	}

	public static class AreaMarkGroup implements INBTSerializable<NBTTagCompound>
	{
		private final BlockPos[] definingCorners;
		private int defined;

		private final List<UUID> tapeEdges;

		private AreaMarkGroup()
		{
			this.tapeEdges = new ArrayList<UUID>(12);
			this.definingCorners = new BlockPos[4];
			this.defined = 0;
		}

		@Nullable
		public AreaMarkGroup expand(BlockPos other)
		{
			if(this.defined == 1) // singleton -> line
			{
				BlockPos bl = this.definingCorners[0];
				if(Objects.equals(bl, other) || BlockPosUtil.getSharedAxis(bl, other) == null)
					return null;

				AreaMarkGroup ng = new AreaMarkGroup();
				ng.definingCorners[0] = bl;
				ng.definingCorners[1] = other;
				ng.defined = 2;
				return ng;
			}
			else if(this.defined == 2) // line -> area
			{
				BlockPos b1 = this.definingCorners[0];
				BlockPos b2 = this.definingCorners[1];

				Axis3d def = BlockPosUtil.getSharedAxis(b1, b2);

				Axis3d a1 = BlockPosUtil.getSharedAxis(b1, other);
				Axis3d a2 = BlockPosUtil.getSharedAxis(b2, other);

				if((a1 == null && a2 == null) || a1 == def || a2 == def)
					return null;

				AreaMarkGroup ng = new AreaMarkGroup();
				ng.definingCorners[0] = b1;
				ng.definingCorners[1] = b2;
				ng.definingCorners[2] = other;
				ng.defined = 3;
				return ng;
			}
			else if(this.defined == 3) // area -> volume
			{
				BlockPos b1 = this.definingCorners[0];
				BlockPos b2 = this.definingCorners[1];
				BlockPos b3 = this.definingCorners[2];

				Set<Axis3d> covered = EnumSet.noneOf(Axis3d.class);
				Axis3d a12 = BlockPosUtil.getSharedAxis(b1, b2);
				Axis3d a23 = BlockPosUtil.getSharedAxis(b2, b3);
				Axis3d a13 = BlockPosUtil.getSharedAxis(b1, b3);
				if(a12 != null)
					covered.add(a12);
				if(a23 != null)
					covered.add(a23);
				if(a13 != null)
					covered.add(a13);

				Axis3d c1 = BlockPosUtil.getSharedAxis(b1, other);
				Axis3d c2 = BlockPosUtil.getSharedAxis(b2, other);
				Axis3d c3 = BlockPosUtil.getSharedAxis(b3, other);

				if((c1 == null && c2 == null && c3 == null) || covered.contains(c1) || covered.contains(c2) || covered.contains(c3))
					return null;

				AreaMarkGroup ng = new AreaMarkGroup();
				ng.definingCorners[0] = b1;
				ng.definingCorners[1] = b2;
				ng.definingCorners[2] = b3;
				ng.definingCorners[3] = other;
				ng.defined = 4;
				return ng;
			}
			else
			{
				return null;
			}
		}

		public boolean isComplete()
		{
			return this.defined == 4;
		}

		public Iterable<BlockPos> getDefinedCorners()
		{
			return ArrayIterable.wrap(this.definingCorners, 0, this.defined);
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

		public void dismantle(World world)
		{
			for(BlockPos dc : this.getDefinedCorners())
			{
				TileEntityAreaMarker marker = (TileEntityAreaMarker)world.getTileEntity(dc);
				if(marker == null)
					throw new IllegalStateException("Marker tile entity is null");
				marker.group = AreaMarkGroup.voxel(marker.getPos());
			}

			List<EntityConstructionTape> tapeEdges = world.getEntities(EntityConstructionTape.class, (EntityConstructionTape e) -> this.tapeEdges.contains(e.getUniqueID()));
			for(EntityConstructionTape tape : tapeEdges)
				tape.dismantle();
		}

		public void construct(World world)
		{
			for(BlockPos dc : this.getDefinedCorners())
			{
				TileEntityAreaMarker marker = (TileEntityAreaMarker)world.getTileEntity(dc);
				if(marker == null)
					throw new IllegalStateException("Marker tile entity is null");
				marker.group = this;
			}

			for(BlockPosEdge edge : this.edges())
			{
				Vec3d anchor1 = new Vec3d(edge.getFirst()).add(0.5, 0.5, 0.5);
				Vec3d anchor2 = new Vec3d(edge.getSecond()).add(0.5, 0.5, 0.5);
				EntityConstructionTape tape = new EntityConstructionTape(world);
				tape.setAnchor(anchor1, anchor2);
				world.spawnEntity(tape);
				this.tapeEdges.add(tape.getUniqueID());
			}
		}

		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList tapes = new NBTTagList();
			for(UUID tape : this.tapeEdges)
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
			this.tapeEdges.clear();
			NBTTagList tapeList = nbt.getTagList("tape", 10);
			for(int index = 0; index < tapeList.tagCount(); ++index)
				this.tapeEdges.add(NBTUtil.getUUIDFromTag(tapeList.getCompoundTagAt(index)));

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
}
