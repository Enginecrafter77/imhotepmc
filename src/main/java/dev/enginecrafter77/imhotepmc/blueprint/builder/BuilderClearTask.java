package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.EnumWorldEvent;
import dev.enginecrafter77.imhotepmc.util.energy.EnergyExtractTransaction;
import dev.enginecrafter77.imhotepmc.util.energy.EnergyTransaction;
import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackInsertTransaction;
import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackTransaction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BuilderClearTask extends AbstractPoweredBuilderTask {
	public BuilderClearTask(World world, BlockPos pos, BuilderContext context)
	{
		super(world, pos, context);
	}

	@Override
	protected ItemStackTransaction createItemStackTransaction()
	{
		return new ItemStackInsertTransaction(this.context.getBOMProvider().getBlockClearReclaimedItems(this.world, this.pos));
	}

	@Override
	protected EnergyTransaction createEnergyTransaction()
	{
		float hardness = this.getBlockToBreak().getBlockHardness(this.world, this.pos);
		int energy = Math.round(Math.min(500F, hardness * 100F));
		return new EnergyExtractTransaction(energy);
	}

	public IBlockState getBlockToBreak()
	{
		return this.world.getBlockState(this.pos);
	}

	@Override
	public void performTask()
	{
		super.performTask();
		EnumWorldEvent.BLOCK_BREAK.play(this.world, this.pos, Block.getStateId(this.getBlockToBreak()));
		this.world.setBlockToAir(this.pos);
	}
}
