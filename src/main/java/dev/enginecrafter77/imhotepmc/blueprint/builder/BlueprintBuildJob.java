package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.stream.Stream;

public class BlueprintBuildJob extends StructureBuildJob {
	private final BlueprintPlacement placement;

	public BlueprintBuildJob(BuilderContext context, BlueprintPlacement placement)
	{
		super(context, placement.getOriginOffset(), placement.getSize());
		this.placement = placement;
	}

	public BlueprintPlacement getPlacement()
	{
		return this.placement;
	}

	@Override
	public BuilderTask createTask(BlockPos pos)
	{
		BlueprintEntry entry = this.placement.getBlockAt(pos);
		BuilderBlockPlacementDetails details = BuilderBlockPlacementDetails.fromBlueprintEntry(entry);
		return new BuilderPlaceTask(this.context, pos, details);
	}

	public Stream<ItemStack> currentlyMissingItems()
	{
		if(this.isDone())
			return Stream.empty();
		AbstractBuilderTask task = (AbstractBuilderTask)this.getCurrentTask();
		if(task == null)
			return Stream.empty();
		return task.missingItems();
	}

	@Override
	public TaskAction getTaskActionFor(BlockPos pos)
	{
		IBlockState currentBlock = this.getWorld().getBlockState(pos);
		BlueprintEntry entry = this.placement.getBlockAt(pos);
		if(Objects.equals(entry.getBlock(), currentBlock.getBlock()))
			return TaskAction.SKIP;
		Block block = entry.getBlock();
		if(block == null)
			return TaskAction.SKIP; // block failed to resolve, hard skip
		if(!block.canPlaceBlockAt(this.getWorld(), pos))
			return TaskAction.DEFER;
		return TaskAction.PROCEED;
	}
}
