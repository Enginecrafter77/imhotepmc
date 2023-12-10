package dev.enginecrafter77.imhotepmc.blueprint.builder;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultBOMProvider implements BuilderBOMProvider {
	private final Map<Block, ItemStack> overrides;

	public DefaultBOMProvider()
	{
		this.overrides = new HashMap<Block, ItemStack>();
	}

	public void addOverride(Block block, ItemStack item)
	{
		this.overrides.put(block, item);
	}

	public void addOverride(Block block, Item item)
	{
		this.addOverride(block, new ItemStack(item));
	}

	@Override
	public Collection<ItemStack> getBlockPlaceRequiredItems(World world, BlockPos placeAt, IBlockState blockStateToPlace, @Nullable TileEntity tileEntity)
	{
		Block block = blockStateToPlace.getBlock();
		if(block instanceof BlockDoor && blockStateToPlace.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
			return ImmutableList.of();

		ItemStack stack = this.overrides.get(block);
		if(stack == null)
			stack = new ItemStack(block);
		return Collections.singleton(stack);
	}

	@Override
	public Collection<ItemStack> getBlockClearReclaimedItems(World world, BlockPos clearAt)
	{
		IBlockState state = world.getBlockState(clearAt);
		TileEntity tileEntity = world.getTileEntity(clearAt);
		return this.getBlockPlaceRequiredItems(world, clearAt, state, tileEntity);
	}
}
