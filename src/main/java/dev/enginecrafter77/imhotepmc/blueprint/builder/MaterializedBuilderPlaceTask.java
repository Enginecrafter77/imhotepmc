package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public class MaterializedBuilderPlaceTask extends BaseBuilderPlaceTask {
	protected final BuilderMaterialProvider storageProvider;
	protected final BuilderBOMProvider bomProvider;

	private Collection<ItemStack> requiredItems;

	public MaterializedBuilderPlaceTask(World world, BlockPos pos, BuilderBlockPlacementDetails details, BuilderMaterialProvider storageProvider, BuilderBOMProvider bomProvider)
	{
		super(world, pos, details);
		this.storageProvider = storageProvider;
		this.bomProvider = bomProvider;
		this.updateRequiredItems();
	}

	public void updateRequiredItems()
	{
		IBlockState state = this.getStateForPlacement();
		if(state == null)
			throw new IllegalStateException();
		this.requiredItems = this.bomProvider.getBlockPlaceRequiredItems(this.world, this.pos, state, this.createTileEntity());
	}

	@Override
	public boolean canBeExecuted()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return false;
		return storage.canProvide(this.requiredItems);
	}

	@Override
	public void executeTask()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return;
		storage.provide(this.requiredItems);
		super.executeTask();
	}
}
