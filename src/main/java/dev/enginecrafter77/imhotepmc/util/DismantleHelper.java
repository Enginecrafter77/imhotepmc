package dev.enginecrafter77.imhotepmc.util;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class DismantleHelper {
	public static DropConfigurator dismantle(World world, BlockPos pos, boolean returnDrops)
	{
		return new DropConfigurator(world, pos, returnDrops);
	}

	public static class DropConfigurator
	{
		private final ArrayList<ItemStack> drops;
		private final World world;
		private final BlockPos pos;
		private final boolean returnDrops;

		public DropConfigurator(World world, BlockPos pos, boolean returnDrops)
		{
			this.drops = new ArrayList<>();
			this.world = world;
			this.pos = pos;
			this.returnDrops = returnDrops;
		}

		public DropConfigurator drop(ItemStack stack)
		{
			this.drops.add(stack);
			return this;
		}

		public DropConfigurator drop(Block block)
		{
			return this.drop(new ItemStack(Item.getItemFromBlock(block)));
		}

		public ArrayList<ItemStack> go()
		{
			this.world.setBlockToAir(this.pos);
			if(!this.returnDrops && !this.world.isRemote)
			{
				for(ItemStack stack : this.drops)
				{
					EntityItem item = new EntityItem(this.world);
					item.setPosition(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D);
					item.setItem(stack);
					this.world.spawnEntity(item);
				}
			}
			return this.drops;
		}
	}
}
