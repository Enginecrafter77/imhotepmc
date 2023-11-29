package dev.enginecrafter77.imhotepmc.gui;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.container.ContainerArchitectTable;
import dev.enginecrafter77.imhotepmc.tile.TileEntityArchitectTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableRectangle;
import org.lwjgl.util.Rectangle;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;

public class GUIArchitectTable extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ImhotepMod.MOD_ID, "textures/gui/gui_architect_table.png");
	private static final ITextComponent DEFAULT_TITLE = new TextComponentTranslation("tile.architect_table.name");

	private static final int GUI_BUTTON_ID_SAVE = 0;
	private static final int GUI_COMPONENT_ID_NAME = 1;
	private static final int GUI_COMPONENT_ID_DESC = 2;

	private static final ReadableRectangle TITLE_RECT = new Rectangle(7, 6, 159, 10);
	private static final ReadableRectangle NAME_LABEL_RECT = new Rectangle(7, 21, 112, 10);
	private static final ReadableRectangle DESC_LABEL_RECT = new Rectangle(7, 62, 112, 10);

	private final TileEntityArchitectTable tile;
	private final InventoryPlayer inventoryPlayer;

	private final Rectangle drawRect;
	private final Point guiOffset;

	private GuiButton saveButton;
	private GuiTextField nameField;
	private GuiTextField descField;

	public GUIArchitectTable(InventoryPlayer inventoryPlayer, ContainerArchitectTable container, TileEntityArchitectTable tile)
	{
		super(container);
		this.drawRect = new Rectangle();
		this.guiOffset = new Point();
		this.inventoryPlayer = inventoryPlayer;
		this.tile = tile;

		this.xSize = 176;
		this.ySize = 182;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		this.nameField = new GuiTextField(GUI_COMPONENT_ID_NAME, this.fontRenderer, this.guiLeft + 7, this.guiTop + 34, 112, 18);
		this.descField = new GuiTextField(GUI_COMPONENT_ID_DESC, this.fontRenderer, this.guiLeft + 7, this.guiTop + 75, 112, 18);
		this.saveButton = new GuiButtonImage(GUI_BUTTON_ID_SAVE, this.guiLeft + 132, this.guiTop + 45, 18, 18, 176, 0, 18, TEXTURE);

		this.addButton(this.saveButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.nameField.drawTextBox();
		this.descField.drawTextBox();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		GlStateManager.resetColor();
		drawTexturedModalRect(this.guiLeft - 1, this.guiTop - 1, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.renderHoveredToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);

		GuiUtil.drawStringInRectangle(this, this.fontRenderer, TITLE_RECT, this.getTitle().getUnformattedText(), Color.WHITE.getRGB());
		GuiUtil.drawStringInRectangle(this, this.fontRenderer, NAME_LABEL_RECT, "Name", Color.WHITE.getRGB());
		GuiUtil.drawStringInRectangle(this, this.fontRenderer, DESC_LABEL_RECT, "Description", Color.WHITE.getRGB());
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
		this.descField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
		this.nameField.textboxKeyTyped(typedChar, keyCode);
		this.descField.textboxKeyTyped(typedChar, keyCode);
	}

	@Override
	protected void actionPerformed(@Nonnull GuiButton button) throws IOException
	{
		if(button.id == GUI_BUTTON_ID_SAVE)
		{

			return;
		}
		super.actionPerformed(button);
	}

	@Nonnull
	public ITextComponent getTitle()
	{
		ITextComponent name = this.tile.getDisplayName();
		if(name == null)
			name = DEFAULT_TITLE;
		return name;
	}
}
