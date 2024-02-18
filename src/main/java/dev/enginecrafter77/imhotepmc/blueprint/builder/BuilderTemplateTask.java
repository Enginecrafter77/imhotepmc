package dev.enginecrafter77.imhotepmc.blueprint.builder;

import dev.enginecrafter77.imhotepmc.util.energy.EnergyExtractTransaction;
import dev.enginecrafter77.imhotepmc.util.energy.EnergyTransaction;
import dev.enginecrafter77.imhotepmc.util.inventory.ItemStackTransaction;
import dev.enginecrafter77.imhotepmc.util.inventory.MatchingExtractItemStackTransaction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BuilderTemplateTask extends AbstractPoweredBuilderTask {
	protected int updateFlags;

	public BuilderTemplateTask(World world, BlockPos pos, BuilderContext context)
	{
		super(world, pos, context);
		this.updateFlags = 3;
	}

	@Override
	protected ItemStackTransaction createItemStackTransaction()
	{
		return new MatchingExtractItemStackTransaction(this::isItemStackSuitableTemplateItem, 1);
	}

	@Override
	protected EnergyTransaction createEnergyTransaction()
	{
		return new EnergyExtractTransaction(1000);
	}

	public void setUpdateFlags(int updateFlags)
	{
		this.updateFlags = updateFlags;
	}

	@Override
	public void performTask()
	{
		super.performTask();

		ItemStack extracted = this.getItemStackTransaction().getTransactionStacks().stream().findFirst().orElseThrow(IllegalStateException::new); // you're lying
		@SuppressWarnings("deprecation") IBlockState state = ((ItemBlock)extracted.getItem()).getBlock().getStateFromMeta(extracted.getMetadata());
		this.world.setBlockState(this.pos, state, this.updateFlags);
	}

	protected boolean isItemStackSuitableTemplateItem(ItemStack stack)
	{
		Item item = stack.getItem();
		if(!(item instanceof ItemBlock))
			return false;
		Block blk = ((ItemBlock)item).getBlock();
		@SuppressWarnings("deprecation") IBlockState state = blk.getStateFromMeta(stack.getMetadata());
		return state.isFullCube();
	}
}
