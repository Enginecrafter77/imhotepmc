package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaterializedBuilderClearTask extends BaseBuilderClearTask {
	private final BuilderMaterialStorageProvider storageProvider;

	private final NonNullList<ItemStack> drops;

	public MaterializedBuilderClearTask(World world, BlockPos pos, BuilderMaterialStorageProvider storageProvider)
	{
		super(world, pos);
		this.storageProvider = storageProvider;

		this.drops = NonNullList.create();
	}

	protected void reloadDrops()
	{
		this.drops.clear();
		IBlockState state = this.world.getBlockState(this.pos);
		state.getBlock().getDrops(this.drops, this.world, this.pos, state, 0);
	}

	@Override
	public boolean canBeExecuted()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return false;

		this.reloadDrops();
		for(ItemStack drop : this.drops)
		{
			if(!storage.canInsert(drop))
				return false;
		}
		return true;
	}

	@Override
	public void executeTask()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return;
		super.executeTask();
		this.drops.forEach(storage::addBlockDrops);
	}
}
