package dev.enginecrafter77.imhotepmc.gui;

import dev.enginecrafter77.imhotepmc.tile.TileEntityTerraformer;
import net.minecraft.client.gui.GuiScreen;

public class GUITerraformer extends GuiScreen {
	private final TileEntityTerraformer tileEntity;

	public GUITerraformer(TileEntityTerraformer tileEntity)
	{
		this.tileEntity = tileEntity;
	}
}
