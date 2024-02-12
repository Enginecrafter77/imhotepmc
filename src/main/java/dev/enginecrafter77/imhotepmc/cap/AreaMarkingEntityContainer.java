package dev.enginecrafter77.imhotepmc.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AreaMarkingEntityContainer implements ICapabilitySerializable<NBTBase> {
	private final AreaMarkingEntity job;

	public AreaMarkingEntityContainer()
	{
		this.job = CapabilityAreaMarker.AREA_MARKER.getDefaultInstance();
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityAreaMarker.AREA_MARKER;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityAreaMarker.AREA_MARKER)
			return CapabilityAreaMarker.AREA_MARKER.cast(this.job);
		return null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return CapabilityAreaMarker.AREA_MARKER.writeNBT(this.job, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		CapabilityAreaMarker.AREA_MARKER.readNBT(this.job, null, nbt);
	}
}
