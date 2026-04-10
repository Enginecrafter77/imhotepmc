package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.util.EnumWorldEvent;
import dev.enginecrafter77.imhotepmc.util.transaction.ItemStackTransactionTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BuilderClearTask extends AbstractBuilderTask {
	public BuilderClearTask(BuilderContext context, BlockPos pos)
	{
		super(context, pos);
	}

	@Override
	public ItemStackTransactionTemplate createItemStackTransactionTemplate()
	{
		return ItemStackTransactionTemplate.builder()
				.recoverAll(ImhotepMod.instance.getBuilderBomProvider().getBlockClearReclaimedItems(this.getWorld(), this.pos))
				.build();
	}

	@Override
	public int getEnergyRequired()
	{
		float hardness = this.getBlockToBreak().getBlockHardness(this.getWorld(), this.pos);
		return Math.round(Math.min(500F, hardness * 100F));
	}

	public IBlockState getBlockToBreak()
	{
		return this.getWorld().getBlockState(this.pos);
	}

	@Override
	public void performTask()
	{
		EnumWorldEvent.BLOCK_BREAK.play(this.getWorld(), this.pos, Block.getStateId(this.getBlockToBreak()));
		this.getWorld().setBlockToAir(this.pos);
	}
}
