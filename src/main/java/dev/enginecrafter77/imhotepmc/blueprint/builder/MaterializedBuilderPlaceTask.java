package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

public class MaterializedBuilderPlaceTask extends BaseBuilderPlaceTask {
	protected final BuilderMaterialProvider storageProvider;
	protected final BuilderBOMProvider bomProvider;
	private final Collection<ItemStack> requiredItems;

	@Nullable
	private ItemStackTransaction transaction;

	public MaterializedBuilderPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details, BuilderMaterialProvider storageProvider, BuilderBOMProvider bomProvider)
	{
		super(world, pos, details);
		this.transaction = null;
		this.storageProvider = storageProvider;
		this.bomProvider = bomProvider;

		IBlockState state = this.getStateForPlacement();
		if(state == null)
			throw new IllegalStateException();
		this.requiredItems = this.bomProvider.getBlockPlaceRequiredItems(this.world, this.pos, state, this.createTileEntity());
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
			this.transaction = storage.consume(this.requiredItems);
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
