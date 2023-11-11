package dev.enginecrafter77.imhotepmc.blueprint;

import dev.enginecrafter77.imhotepmc.util.BlockSelectionBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ScanningBlueprintFactory implements BlueprintFactory {
	private final BlockSelectionBox selection;
	private final IBlockAccess world;

	public ScanningBlueprintFactory(IBlockAccess world)
	{
		this.world = world;
		this.selection = new BlockSelectionBox();
	}

	public void setSelection(BlockSelectionBox box)
	{
		this.selection.set(box);
	}

	@Override
	public StructureBlueprint createBlueprint()
	{
		StructureBlueprint.Builder builder = StructureBlueprint.builder();

		for(BlockPos.MutableBlockPos pos : this.selection.volume())
		{
			SavedTileState data = SavedTileState.sample(this.world, pos);
			builder.addBlock(pos, data);
		}

		return builder.build();
	}
}
