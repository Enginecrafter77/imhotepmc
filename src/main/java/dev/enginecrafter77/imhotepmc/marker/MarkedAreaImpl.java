package dev.enginecrafter77.imhotepmc.marker;

import dev.enginecrafter77.imhotepmc.util.BlockPosUtil;
import dev.enginecrafter77.imhotepmc.util.math.Box3i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class MarkedAreaImpl implements MarkedArea, INBTSerializable<NBTTagCompound> {
	private static final String NBT_KEY_ID = "id";
	private static final String NBT_KEY_CORNERS = "corners";

	private final AreaMarkBox area;

	@Nonnull
	private UUID id;

	public MarkedAreaImpl(UUID id)
	{
		this.area = new AreaMarkBox();
		this.id = id;
	}

	public MarkedAreaImpl()
	{
		this(UUID.randomUUID());
	}

	@Override
	public UUID getId()
	{
		return this.id;
	}

	@Override
	public Collection<BlockPos> getDefiningMembers()
	{
		return this.area.getDefiningCorners();
	}

	@Override
	public Box3i getMarkedAreaBox()
	{
		return this.area.getBox();
	}

	public void set(MarkedAreaImpl other)
	{
		this.id = other.id;
		this.area.set(other.area);
	}

	public AreaExpandResult expand(BlockPos other, boolean simulate)
	{
		AreaExpandResult result = this.area.evaluateNewMember(other);
		if(result == AreaExpandResult.SUCCESS && !simulate)
			this.area.add(other);
		return result;
	}

	public void remove(BlockPos other)
	{
		this.area.remove(other);
	}

	public boolean isComplete()
	{
		return this.area.isFullyDefined();
	}

	public boolean isEmpty()
	{
		return this.area.isEmpty();
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for(BlockPos pos : this.area.getDefiningCorners())
			list.appendTag(NBTUtil.createPosTag(pos));
		tag.setTag(NBT_KEY_CORNERS, list);
		tag.setUniqueId(NBT_KEY_ID, this.id);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		UUID readId = nbt.getUniqueId(NBT_KEY_ID);
		if(readId != null)
			this.id = readId;
		NBTTagList list = nbt.getTagList(NBT_KEY_CORNERS, 10);
		for(int i = 0; i < list.tagCount(); ++i)
			this.area.add(NBTUtil.getPosFromTag(list.getCompoundTagAt(i)));
	}

	public static MarkedAreaImpl create(BlockPos first, BlockPos second)
	{
		assert !Objects.equals(first, second) && BlockPosUtil.getSharedAxis(first, second) != null;
		MarkedAreaImpl group = new MarkedAreaImpl(UUID.randomUUID());
		group.expand(first, false);
		group.expand(second, false);
		return group;
	}
}
