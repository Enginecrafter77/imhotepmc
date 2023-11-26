package dev.enginecrafter77.imhotepmc.tile;

import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

public class TileEntityBuilder extends TileEntity implements ITickable {
	@Nullable
	private SchematicBlueprint blueprint;

	@Nullable
	private IterativeBuilder builder;

	public TileEntityBuilder()
	{
		this.blueprint = null;
		this.builder = null;
	}

	@Override
	public void update()
	{

	}

	public static class IterativeBuilder
	{
		private final SchematicBlueprint blueprint;

		public IterativeBuilder()
		{
			this.blueprint = null;
		}
	}
}
