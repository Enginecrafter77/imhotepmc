package dev.enginecrafter77.imhotepmc.marker;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class AreaMarkingActorWrapper implements ICapabilitySerializable<NBTBase> {
	private final AreaMarkingActor handler;

	public AreaMarkingActorWrapper()
	{
		this.handler = CapabilityAreaMarker.AREA_MARKING_ACTOR.getDefaultInstance();
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityAreaMarker.AREA_MARKING_ACTOR;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityAreaMarker.AREA_MARKING_ACTOR)
			return CapabilityAreaMarker.AREA_MARKING_ACTOR.cast(this.handler);
		return null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return Objects.requireNonNull(CapabilityAreaMarker.AREA_MARKING_ACTOR.writeNBT(this.handler, null));
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		CapabilityAreaMarker.AREA_MARKING_ACTOR.readNBT(this.handler, null, nbt);
	}
}
