package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaterializedBuilderPlaceTask extends BaseBuilderPlaceTask {
	protected final BuilderMaterialProvider storageProvider;

	public MaterializedBuilderPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details, BuilderMaterialProvider storageProvider)
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
		return storage.canProvide(this.details.getBlock());
	}

	@Override
	public void executeTask()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return;
		storage.provide(this.details.getBlock());
		super.executeTask();
	}
}
