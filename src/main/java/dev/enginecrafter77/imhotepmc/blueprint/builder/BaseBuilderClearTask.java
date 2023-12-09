package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BaseBuilderClearTask extends AbstractBuilderTask {
	public BaseBuilderClearTask(World world, BlockPos pos)
	{
		super(world, pos);
	}

	@Override
	public boolean canBeExecuted()
	{
		return true;
	}

	@Override
	public void executeTask()
	{
		this.world.setBlockToAir(this.pos);
	}
}
