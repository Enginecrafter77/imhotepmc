package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaterializedBuilderClearTask extends BaseBuilderClearTask {
	private final BuilderMaterialProvider storageProvider;

	public MaterializedBuilderClearTask(World world, BlockPos pos, BuilderMaterialProvider storageProvider)
	{
		super(world, pos);
		this.storageProvider = storageProvider;
	}

	@Override
	public boolean canBeExecuted()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return false;
		IBlockState state = this.world.getBlockState(this.pos);
		return storage.canReclaim(state.getBlock());
	}

	@Override
	public void executeTask()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return;
		IBlockState state = this.world.getBlockState(this.pos);
		storage.reclaim(state.getBlock());
		super.executeTask();
	}
}
