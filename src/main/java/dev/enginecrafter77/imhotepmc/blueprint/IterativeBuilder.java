package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class IterativeBuilder {
	private IterativeBuilderState state;

	public IterativeBuilder(SchematicBlueprint blueprint)
	{
		this.state = new IterativeBuilderState();
		this.state.regionOrder = StreamSupport.stream(blueprint.getRegions().spliterator(), false).collect(Collectors.toList());
		this.state.region = 0;
	}

	public void restoreState(NBTTagCompound tag)
	{
		this.state.deserializeNBT(tag);
	}

	public NBTTagCompound saveState()
	{
		return this.state.serializeNBT();
	}

	private static class IterativeBuilderState implements INBTSerializable<NBTTagCompound>
	{
		private List<String> regionOrder;
		private int region;

		@Override
		public NBTTagCompound serializeNBT()
		{
			return null;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{

		}
	}
}
