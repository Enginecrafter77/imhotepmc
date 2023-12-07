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

	private final BuilderHandler handler;
	private final ShapeGenerator generator;
	private final BlockSelectionBox area;
	private final VoxelIndexer indexer;

	private int index;

	public ShapeBuilder(BlockSelectionBox area, ShapeGenerator generator, ShapeBuildStrategy strategy, BuilderHandler handler)
	{
		this.indexer = strategy.createVoxelIndexer(area.getMinCorner(), area.getSize());
		this.generator = generator;
		this.handler = handler;
		this.area = area;
		this.index = -1;
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

		ShapeGenerator.ShapeGeneratorAction action;
		BlockPos pos;
		do
		{
			++newindex;

			if(newindex >= this.indexer.getVolume())
			{
				this.index = newindex;
				return;
			}

			pos = this.indexer.fromIndex(newindex);
			action = this.generator.blockActionFor(this.area, pos);

			if(action == ShapeGenerator.ShapeGeneratorAction.CLEAR && world.getBlockState(pos).getBlock() == Blocks.AIR)
				action = ShapeGenerator.ShapeGeneratorAction.PASS;
		}
		while(action == ShapeGenerator.ShapeGeneratorAction.PASS);

		if(action == ShapeGenerator.ShapeGeneratorAction.PLACE)
		{
			Block blk = this.handler.getAvailableBlock();
			if(blk == null)
				return;
			IBlockState blks = blk.getDefaultState();
			if(!this.handler.onPlaceBlock(world, pos, blks))
				return;
			world.setBlockState(pos, blks, 3);
		}
		else if(action == ShapeGenerator.ShapeGeneratorAction.CLEAR)
		{
			IBlockState blks = world.getBlockState(pos);
			if(!this.handler.onClearBlock(world, pos, blks))
				return;
			world.setBlockToAir(pos);
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
