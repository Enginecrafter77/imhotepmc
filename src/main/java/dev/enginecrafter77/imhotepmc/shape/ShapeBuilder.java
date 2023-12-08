package dev.enginecrafter77.imhotepmc.shape;

import dev.enginecrafter77.imhotepmc.blueprint.StructureBuilder;
import dev.enginecrafter77.imhotepmc.blueprint.VoxelIndexer;
import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShapeBuilder implements StructureBuilder {
	private static final String NBT_KEY_INDEX = "index";

	private final BuilderHost handler;
	private final ShapeGenerator generator;
	private final BlockSelectionBox area;
	private final ShapeBuildMode buildMode;
	private final VoxelIndexer indexer;

	private int index;

	public ShapeBuilder(BlockSelectionBox area, ShapeGenerator generator, ShapeBuildMode buildMode, BuilderHost handler)
	{
		this.indexer = buildMode.createVoxelIndexer(area);
		this.generator = generator;
		this.handler = handler;
		this.buildMode = buildMode;
		this.area = area;
		this.index = -1;
	}

	public ShapeBuildMode getBuildMode()
	{
		return this.buildMode;
	}

	@Override
	public boolean isReady()
	{
		return true;
	}

	@Override
	public boolean isFinished()
	{
		return (this.index + 1) >= this.indexer.getVolume();
	}

	@Override
	public void tryPlaceNextBlock(World world)
	{
		int newindex = this.index;

		while((newindex + 1) < this.indexer.getVolume())
		{
			BuilderAction action = this.buildMode.getBuilderAction(this.handler);
			if(action == BuilderAction.STALL)
				return;
			if(action == BuilderAction.PASS)
				continue;

			++newindex;
			BlockPos pos = this.indexer.fromIndex(newindex);
			boolean included = this.generator.isBlockInShape(this.area, pos);

			if(!included)
				continue;

			IBlockState state = world.getBlockState(pos);
			if(action == BuilderAction.PLACE)
			{
				Block blk = this.handler.getAvailableBlock();
				if(blk == null)
					break;
				if(blk == state.getBlock())
					continue;

				state = blk.getDefaultState();
				if(!this.handler.onPlaceBlock(world, pos, state))
					return;
				world.setBlockState(pos, state, 3);
				break;
			}
			else if(action == BuilderAction.CLEAR)
			{
				if(state.getBlock() == Blocks.AIR)
					continue;
				if(!this.handler.onClearBlock(world, pos, state))
					return;
				world.setBlockToAir(pos);
				break;
			}
		}

		this.index = newindex;
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
