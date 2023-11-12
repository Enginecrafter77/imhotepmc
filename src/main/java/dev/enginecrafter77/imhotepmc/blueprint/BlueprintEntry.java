package dev.enginecrafter77.imhotepmc.blueprint;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;

public interface BlueprintEntry {
	public ResourceLocation getBlockName();

	public Map<String, String> getBlockProperties();

	@Nullable
	public NBTTagCompound getTileEntitySavedData();

	@Nullable
	public Block getBlock();

	@Nullable
	public IBlockState createBlockState();

	public boolean hasTileEntity();

	@Nullable
	public TileEntity createTileEntity(World world);

	public boolean equals(Object other);

	public int hashCode();
}
