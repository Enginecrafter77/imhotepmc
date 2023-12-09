package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MaterializedBuilderPlaceTask extends BaseBuilderPlaceTask {
	protected final BuilderMaterialStorageProvider storageProvider;

	public MaterializedBuilderPlaceTask(World world, BlockPos pos, Block block, @Nullable NBTTagCompound tileSavedData, BuilderMaterialStorageProvider storageProvider)
	{
		super(world, pos, block, tileSavedData);
		this.storageProvider = storageProvider;
	}

	@Override
	public boolean canBeExecuted()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return false;
		return storage.hasBlock(this.block);
	}

	@Override
	public void executeTask()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return;
		storage.consumeBlock(this.block);
		super.executeTask();
	}
}
