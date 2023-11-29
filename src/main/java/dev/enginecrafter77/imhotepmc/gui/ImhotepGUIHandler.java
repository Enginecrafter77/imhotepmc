package dev.enginecrafter77.imhotepmc.gui;

import dev.enginecrafter77.imhotepmc.container.ContainerArchitectTable;
import dev.enginecrafter77.imhotepmc.container.ContainerBlueprintLibrary;
import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBlueprintLibrary;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class ImhotepGUIHandler implements IGuiHandler {
	public static final int GUI_ID_BLUEPRINT_LIBRARY = 0;
	public static final int GUI_ID_ARCHITECT_TABLE = 1;

	@Nullable
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
		case GUI_ID_BLUEPRINT_LIBRARY:
			TileEntityBlueprintLibrary library = (TileEntityBlueprintLibrary)world.getTileEntity(new BlockPos(x, y, z));
			if(library == null)
				throw new IllegalStateException("Tile entity not found");
			return new ContainerBlueprintLibrary(player.inventory, library);
		case GUI_ID_ARCHITECT_TABLE:
			return new ContainerArchitectTable(player.inventory);
		default:
			return null;
		}
	}

	@Nullable
	@Override
	public Gui getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);

		switch(ID)
		{
		case GUI_ID_BLUEPRINT_LIBRARY:
			if(tile == null)
				throw new IllegalStateException("Tile entity not found");
			ContainerBlueprintLibrary containerBlueprintLibrary = new ContainerBlueprintLibrary(player.inventory, (TileEntityBlueprintLibrary)tile);
			return new GUIBlueprintLibrary(player.inventory, (TileEntityBlueprintLibrary)tile, containerBlueprintLibrary);
		case GUI_ID_ARCHITECT_TABLE:
			ContainerArchitectTable container = new ContainerArchitectTable(player.inventory);
			return new GUIArchitectTable(player.inventory, container, (TileEntityArchitectTable)tile);
		default:
			return null;
		}
	}
}
