package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.blueprint.BlueprintEntry;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlueprintBuildJob extends StructureBuildJob {
	private final BuilderContext context;
	private final BlueprintPlacement placement;

	public BlueprintBuildJob(BlueprintPlacement placement, BuilderContext context)
	{
		super(placement.getOriginOffset(), placement.getSize());
		this.placement = placement;
		this.context = context;
	}

	public BlueprintPlacement getPlacement()
	{
		return this.placement;
	}

	@Nonnull
	@Override
	public BuilderTask createTask(BlockPos pos)
	{
		BlueprintEntry entry = this.placement.getBlockAt(pos);
		BuilderBlockPlacementDetails details = BuilderBlockPlacementDetails.fromBlueprintEntry(entry);
		return new BuilderPlaceTask(Objects.requireNonNull(this.world), pos, details, this.context);
	}

	@Override
	public boolean shouldBeSkipped(BlockPos pos)
	{
		BlueprintEntry entry = this.placement.getBlockAt(pos);
		return Objects.equals(entry.getBlock(), Blocks.AIR);
	}

	@Override
	public boolean shouldBeDeferred(BlockPos pos)
	{
		BlueprintEntry entry = this.placement.getBlockAt(pos);
		Block block = entry.getBlock();
		if(block == null)
			return false;
		return !block.canPlaceBlockAt(Objects.requireNonNull(this.world), pos);
	}
}
