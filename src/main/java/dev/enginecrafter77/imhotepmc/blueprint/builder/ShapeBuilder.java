package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ShapeBuilder implements StructureBuilder {
	private static final String NBT_KEY_INDEX = "index";

	private final BuilderHandler handler;
	private final ShapeGenerator generator;
	private final BlockSelectionBox area;
	private final ShapeBuildMode buildMode;
	private final VoxelIndexer indexer;

	@Nullable
	private BuilderTask currentTask;

	private int index;

	public ShapeBuilder(BlockSelectionBox area, ShapeGenerator generator, ShapeBuildMode buildMode, BuilderHandler handler)
	{
		this.indexer = buildMode.createVoxelIndexer(area);
		this.generator = generator;
		this.handler = handler;
		this.buildMode = buildMode;
		this.area = area;
		this.currentTask = null;
		this.index = -1;
	}

	public ShapeBuildMode getBuildMode()
	{
		return this.buildMode;
	}

	@Override
	public boolean nextTask(World world)
	{
		this.currentTask = null;
		while(this.currentTask == null && (this.index + 1) < this.indexer.getVolume())
			this.currentTask = this.createTaskFor(world, ++this.index);
		return this.currentTask != null;
	}

	@Nullable
	@Override
	public BuilderTask getLastTask(World world)
	{
		if(this.currentTask == null)
			this.currentTask = this.createTaskFor(world, this.index);
		return this.currentTask;
	}

	@Nullable
	protected BuilderTask createTaskFor(World world, int index)
	{
		if(index < 0 || index >= this.indexer.getVolume())
			return null;

		BlockPos pos = this.indexer.fromIndex(index);
		boolean incl = this.generator.isBlockInShape(this.area, pos);
		if(!incl)
			return null;

		if(this.buildMode.wouldTaskBeInVain(world, pos))
			return null;
		return this.buildMode.createShapeTask(this.handler, world, pos);
	}

	@Override
	public NBTTagCompound saveState()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(NBT_KEY_INDEX, this.index);
		return tag;
	}

	@Override
	public void restoreState(NBTTagCompound nbt)
	{
		this.index = nbt.getInteger(NBT_KEY_INDEX);
	}
}
