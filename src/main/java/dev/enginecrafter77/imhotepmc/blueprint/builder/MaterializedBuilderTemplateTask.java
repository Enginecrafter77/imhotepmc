package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

public class MaterializedBuilderTemplateTask extends BaseBuilderTemplateTask {
	protected final BuilderMaterialProvider storageProvider;
	protected final BuilderBOMProvider bomProvider;

	@Nullable
	private ItemStackTransaction transaction;

	public MaterializedBuilderTemplateTask(World world, BlockPos pos, BuilderMaterialProvider storageProvider, BuilderBOMProvider bomProvider)
	{
		super(world, pos);
		this.storageProvider = storageProvider;
		this.bomProvider = bomProvider;
		this.transaction = null;
	}

	@Nullable
	@Override
	public Block getBlockToPlace()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return null;
		return storage.getAnyAvailableBlock();
	}

	@Override
	public void update()
	{
		this.transaction = null;
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage != null)
		{
			Block available = storage.getAnyAvailableBlock();
			if(available != null)
			{
				IBlockState state = available.getDefaultState();
				Collection<ItemStack> reclaimed = this.bomProvider.getBlockPlaceRequiredItems(this.world, this.pos, state, null);
				this.transaction = storage.consume(reclaimed);
			}
		}
		super.update();
	}

	@Override
	public boolean canBeExecuted()
	{
		return super.canBeExecuted() && this.transaction != null && this.transaction.isCommitable();
	}

	@Override
	public void executeTask()
	{
		if(this.transaction == null)
			return;
		this.transaction.commit();
		super.executeTask();
	}
}
