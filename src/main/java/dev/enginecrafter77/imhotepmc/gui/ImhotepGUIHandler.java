package dev.enginecrafter77.imhotepmc.gui;

import dev.enginecrafter77.imhotepmc.container.ContainerArchitectTable;
import dev.enginecrafter77.imhotepmc.container.ContainerBlueprintLibrary;
import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBlueprintLibrary;
import dev.enginecrafter77.imhotepmc.tile.TileEntityTerraformer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImhotepGUIHandler implements IGuiHandler {
	public static final int GUI_ID_BLUEPRINT_LIBRARY = 0;
	public static final int GUI_ID_ARCHITECT_TABLE = 1;
	public static final int GUI_ID_TERRAFORMER = 2;

	@Nullable
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);

		switch(ID)
		{
		case GUI_ID_BLUEPRINT_LIBRARY:
			return new ContainerBlueprintLibrary(player.inventory, this.obtainTileEntity(TileEntityBlueprintLibrary.class, world, pos));
		case GUI_ID_ARCHITECT_TABLE:
			return new ContainerArchitectTable(player.inventory, this.obtainTileEntity(TileEntityArchitectTable.class, world, pos));
		default:
			return null;
		}
	}

	@Nullable
	@Override
	public Gui getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		switch(ID)
		{
		case GUI_ID_BLUEPRINT_LIBRARY:
			TileEntityBlueprintLibrary libraryTile = this.obtainTileEntity(TileEntityBlueprintLibrary.class, world, pos);
			ContainerBlueprintLibrary containerBlueprintLibrary = new ContainerBlueprintLibrary(player.inventory, libraryTile);
			return new GUIBlueprintLibrary(player.inventory, libraryTile, containerBlueprintLibrary);
		case GUI_ID_ARCHITECT_TABLE:
			TileEntityArchitectTable architectTable = this.obtainTileEntity(TileEntityArchitectTable.class, world, pos);
			ContainerArchitectTable container = new ContainerArchitectTable(player.inventory, architectTable);
			return new GUIArchitectTable(player.inventory, container, architectTable);
		case GUI_ID_TERRAFORMER:
			TileEntityTerraformer tileEntityTerraformer = this.obtainTileEntity(TileEntityTerraformer.class, world, pos);
			return new GUITerraformer(tileEntityTerraformer);
		default:
			return null;
		}
	}

	@Nonnull
	protected <T extends TileEntity> T obtainTileEntity(Class<T> tileClass, IBlockAccess world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null)
			throw new IllegalStateException();
		if(!tileClass.isInstance(tile))
			throw new ClassCastException();
		return tileClass.cast(tile);
	}
}
