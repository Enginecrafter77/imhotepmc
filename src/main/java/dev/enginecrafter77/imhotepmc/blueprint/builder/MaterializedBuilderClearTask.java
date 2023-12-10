package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaterializedBuilderClearTask extends BaseBuilderClearTask {
	private final BuilderMaterialProvider storageProvider;
	private final BuilderBOMProvider bomProvider;

	public MaterializedBuilderClearTask(World world, BlockPos pos, BuilderMaterialProvider storageProvider, BuilderBOMProvider bomProvider)
	{
		super(world, pos);
		this.storageProvider = storageProvider;
		this.bomProvider = bomProvider;
	}

	@Override
	public boolean canBeExecuted()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return false;
		return storage.canReclaim(this.bomProvider.getBlockClearReclaimedItems(this.world, this.pos));
	}

	@Override
	public void executeTask()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return;
		storage.reclaim(this.bomProvider.getBlockClearReclaimedItems(this.world, this.pos));
		super.executeTask();
	}
}
