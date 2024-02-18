package dev.enginecrafter77.imhotepmc.blueprint.builder;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class TileInventoryMaterialStorage implements BuilderMaterialProvider {
	private final IBlockAccess world;
	private final BlockPos pos;

	@Nullable
	private final EnumFacing accessFace;

	public TileInventoryMaterialStorage(IBlockAccess world, BlockPos pos, @Nullable EnumFacing accessFace)
	{
		this.accessFace = accessFace;
		this.world = world;
		this.pos = pos;
	}

	@Nullable
	public TileEntity getTileEntity()
	{
		return this.world.getTileEntity(this.pos);
	}

	@Nullable
	@Override
	public IItemHandler getBuilderInventory()
	{
		TileEntity tile = this.getTileEntity();
		if(tile == null)
			return null;
		return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.accessFace);
	}
}
