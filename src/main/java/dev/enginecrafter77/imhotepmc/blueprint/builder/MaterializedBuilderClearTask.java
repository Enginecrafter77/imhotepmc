package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

public class MaterializedBuilderClearTask extends BaseBuilderClearTask {
	private final BuilderMaterialProvider storageProvider;
	private final BuilderBOMProvider bomProvider;

	@Nullable
	private ItemStackTransaction transaction;

	public MaterializedBuilderClearTask(World world, BlockPos pos, BuilderMaterialProvider storageProvider, BuilderBOMProvider bomProvider)
	{
		super(world, pos);
		this.storageProvider = storageProvider;
		this.bomProvider = bomProvider;
		this.transaction = null;
	}

	@Override
	public void update()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
		{
			this.transaction = null;
		}
		else
		{
			Collection<ItemStack> reclaimed = this.bomProvider.getBlockClearReclaimedItems(this.world, this.pos);
			this.transaction = storage.reclaim(reclaimed);
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

	@Nullable
	public ItemStackTransaction getTransaction()
	{
		return this.transaction;
	}
}
