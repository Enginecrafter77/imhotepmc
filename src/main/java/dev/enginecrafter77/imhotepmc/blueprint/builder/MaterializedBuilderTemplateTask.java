package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MaterializedBuilderTemplateTask extends BaseBuilderTemplateTask {
	protected final BuilderMaterialStorageProvider storageProvider;

	public MaterializedBuilderTemplateTask(World world, BlockPos pos, BuilderMaterialStorageProvider storageProvider)
	{
		super(world, pos);
		this.storageProvider = storageProvider;
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
}
