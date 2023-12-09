package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaterializedBuilderPlaceTask extends BaseBuilderPlaceTask {
	protected final BuilderMaterialStorageProvider storageProvider;

	public MaterializedBuilderPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details, BuilderMaterialStorageProvider storageProvider)
	{
		super(world, pos, details);
		this.storageProvider = storageProvider;
	}

	@Override
	public boolean canBeExecuted()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return false;
		return storage.hasBlock(this.details.getBlock());
	}

	@Override
	public void executeTask()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return;
		storage.consumeBlock(this.details.getBlock());
		super.executeTask();
	}
}
