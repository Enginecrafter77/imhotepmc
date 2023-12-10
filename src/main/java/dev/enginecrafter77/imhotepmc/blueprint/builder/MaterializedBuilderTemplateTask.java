package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class MaterializedBuilderTemplateTask extends BaseBuilderTemplateTask {
	protected final BuilderMaterialProvider storageProvider;
	protected final BuilderBOMProvider bomProvider;

	public MaterializedBuilderTemplateTask(World world, BlockPos pos, BuilderMaterialProvider storageProvider, BuilderBOMProvider bomProvider)
	{
		super(world, pos);
		this.storageProvider = storageProvider;
		this.bomProvider = bomProvider;
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
	public boolean canBeExecuted()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		if(storage == null)
			return false;
		return storage.canProvide(this.bomProvider.getBlockPlaceRequiredItems(this.world, this.pos, Objects.requireNonNull(this.getStateForPlacement()), null));
	}

	@Override
	public void executeTask()
	{
		BuilderMaterialStorage storage = this.storageProvider.getBuilderMaterialStorage();
		IBlockState toPlace = this.getStateForPlacement();
		if(storage == null || toPlace == null)
			return;
		super.executeTask();
		storage.provide(this.bomProvider.getBlockPlaceRequiredItems(this.world, this.pos, toPlace, null));
	}
}
