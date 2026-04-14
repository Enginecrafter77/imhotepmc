package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.marker.MarkingAnchorImpl;
import dev.enginecrafter77.imhotepmc.marker.CapabilityAreaMarker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Objects;

public class TileEntityAreaMarker extends TileEntity {
	private static final String NBT_KEY_MARKER = "marker";

	private final MarkingAnchorImpl areaMarker;

	public TileEntityAreaMarker()
	{
		this.areaMarker = new MarkingAnchorImpl();
		this.areaMarker.setOnDismantleAction(() -> {
			this.world.destroyBlock(this.getPos(), true);
		});
	}

	@Override
	public void setPos(BlockPos posIn)
	{
		super.setPos(posIn);
		this.areaMarker.setPos(posIn);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityAreaMarker.AREA_ANCHOR)
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityAreaMarker.AREA_ANCHOR)
			return CapabilityAreaMarker.AREA_ANCHOR.cast(this.areaMarker);
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if(compound.hasKey(NBT_KEY_MARKER))
			CapabilityAreaMarker.AREA_ANCHOR.readNBT(this.areaMarker, null, compound.getTag(NBT_KEY_MARKER));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setTag("marker", Objects.requireNonNull(CapabilityAreaMarker.AREA_ANCHOR.writeNBT(this.areaMarker, null)));
		return compound;
	}
}
