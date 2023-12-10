package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

public interface BuilderBOMProvider {
	public Collection<ItemStack> getBlockPlaceRequiredItems(World world, BlockPos placeAt, IBlockState blockStateToPlace, @Nullable TileEntity tileEntity);
	public Collection<ItemStack> getBlockClearReclaimedItems(World world, BlockPos clearAt);
}
