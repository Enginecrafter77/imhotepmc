package dev.enginecrafter77.imhotepmc.shape;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class TileBuilderHandler implements BuilderHandler {
	private final IItemHandler storage;
	private final IEnergyStorage energyStorage;

	public TileBuilderHandler(IItemHandler storage, IEnergyStorage energyStorage)
	{
		this.storage = storage;
		this.energyStorage = energyStorage;
	}

	public int getEnergyForPlace(World world, BlockPos pos, IBlockState blockState)
	{
		return 100;
	}

	public int getEnergyForBreak(World world, BlockPos pos, IBlockState blockState)
	{
		return Math.max(50, Math.round(200F * blockState.getBlockHardness(world, pos)));
	}

	@Nullable
	@Override
	public Block getAvailableBlock()
	{
		int slots = this.storage.getSlots();
		for(int slot = 0; slot < slots; ++slot)
		{
			ItemStack stackInSlot = this.storage.getStackInSlot(slot);
			if(stackInSlot.getItem() instanceof ItemBlock)
			{
				ItemBlock blk = (ItemBlock)stackInSlot.getItem();
				return blk.getBlock();
			}
		}
		return null;
	}

	@Override
	public boolean onPlaceBlock(World world, BlockPos pos, IBlockState newBlockState)
	{
		int energy = this.getEnergyForPlace(world, pos, newBlockState);
		int extracted = this.energyStorage.extractEnergy(energy, true);
		if(extracted < energy)
			return false;

		ItemStack consumed = new ItemStack(ItemBlock.getItemFromBlock(newBlockState.getBlock()));
		int slots = this.storage.getSlots();
		for(int slot = 0; slot < slots; ++slot)
		{
			ItemStack stackInSlot = this.storage.getStackInSlot(slot);
			if(ItemStack.areItemsEqual(consumed, stackInSlot))
			{
				this.energyStorage.extractEnergy(energy, false);
				this.storage.extractItem(slot, 1, false);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onClearBlock(World world, BlockPos pos, IBlockState oldBlockState)
	{
		int energy = this.getEnergyForBreak(world, pos, oldBlockState);
		int extracted = this.energyStorage.extractEnergy(energy, true);
		if(extracted < energy)
			return false;

		Block blk = oldBlockState.getBlock();
		NonNullList<ItemStack> drops = NonNullList.create();
		blk.getDrops(drops, world, pos, oldBlockState, 0);
		int[] slots = new int[drops.size()];

		for(int index = 0; index < slots.length; ++index)
		{
			ItemStack stack = drops.get(index);
			slots[index] = this.findSlotForItemStack(stack);
			if(slots[index] == -1)
				return false;
		}

		for(int index = 0; index < slots.length; ++index)
			this.storage.insertItem(slots[index], drops.get(index), false);
		this.energyStorage.extractEnergy(energy, false);
		return true;
	}

	private int findSlotForItemStack(ItemStack stack)
	{
		int slots = this.storage.getSlots();
		for(int slot = 0; slot < slots; ++slot)
		{
			ItemStack stackInSlot = this.storage.getStackInSlot(slot);
			if(stackInSlot.isEmpty() || (ItemStack.areItemsEqual(stack, stackInSlot) && (stackInSlot.getCount() + stack.getCount()) <= stackInSlot.getItem().getItemStackLimit(stackInSlot)))
			{
				return slot;
			}
		}
		return -1;
	}
}
