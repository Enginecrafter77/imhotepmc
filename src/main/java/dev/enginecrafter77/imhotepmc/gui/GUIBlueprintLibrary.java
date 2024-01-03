package dev.enginecrafter77.imhotepmc.gui;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicBlueprint;
import dev.enginecrafter77.imhotepmc.blueprint.SchematicFileFormat;
import dev.enginecrafter77.imhotepmc.container.ContainerBlueprintLibrary;
import dev.enginecrafter77.imhotepmc.net.BlueprintTransferHandler;
import dev.enginecrafter77.imhotepmc.net.stream.client.PacketStreamClientChannel;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBlueprintLibrary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.ReadableRectangle;
import org.lwjgl.util.Rectangle;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class GUIBlueprintLibrary extends GuiContainer {
	private static final Log LOGGER = LogFactory.getLog(GUIBlueprintLibrary.class);

	private static final ResourceLocation TEXTURE = new ResourceLocation(ImhotepMod.MOD_ID, "textures/gui/gui_schematic_library.png");

	private static final int BUTTON_ID_PREV = 0;
	private static final int BUTTON_ID_NEXT = 1;
	private static final int BUTTON_ID_SAVE = 2;
	private static final int BUTTON_ID_LOAD = 3;
	private static final int BUTTON_ID_DELETE = 4;

	private static final ReadableRectangle LIST_ITEM_UNSELECTED = new Rectangle(0, 222, 124, 20);
	private static final ReadableRectangle LIST_ITEM_SELECTED = new Rectangle(124, 222, 124, 20);
	private static final ReadableRectangle BUTTON_PREV = new Rectangle(7, 116, 60, 18);
	private static final ReadableRectangle BUTTON_NEXT = new Rectangle(73, 116, 60, 18);

	private final Rectangle[] itemRectangles;
	private final Rectangle buttonPrevRectangle;
	private final Rectangle buttonNextRectangle;

	private final TileEntityBlueprintLibrary tileEntityBlueprintLibrary;
	private final IItemHandlerModifiable libraryItemHandler;

	private File[] schematics;

	private int pageStart;
	private int selected;

	private GuiButton buttonPrev;
	private GuiButton buttonNext;
	private GuiButton saveButton;
	private GuiButton loadButton;
	private GuiButton deleteButton;

	public GUIBlueprintLibrary(InventoryPlayer inventory, TileEntityBlueprintLibrary library, ContainerBlueprintLibrary container)
	{
		super(container);
		this.tileEntityBlueprintLibrary = library;
		this.libraryItemHandler = (IItemHandlerModifiable)library.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		this.buttonPrevRectangle = new Rectangle(BUTTON_PREV);
		this.buttonNextRectangle = new Rectangle(BUTTON_NEXT);
		this.itemRectangles = new Rectangle[5];
		for(int index = 0; index < itemRectangles.length; ++index)
			this.itemRectangles[index] = new Rectangle();

		this.selected = -1;
		this.pageStart = 0;
		this.xSize = 176;
		this.ySize = 222;
	}

	protected void rescanFiles()
	{
		this.schematics = Objects.requireNonNull(ImhotepMod.instance.getSchematicsDir().listFiles());
		this.setPageStart(0);
		this.setSelected(-1);
	}

	@Override
	public void initGui()
	{
		super.initGui();

		this.buttonNextRectangle.translate(this.guiLeft, this.guiTop);
		this.buttonPrevRectangle.translate(this.guiLeft, this.guiTop);

		this.saveButton = new GuiButtonImage(BUTTON_ID_SAVE, this.guiLeft + 143, this.guiTop + 30, 18, 18, 176, 0, 18, TEXTURE);
		this.loadButton = new GuiButtonImage(BUTTON_ID_LOAD, this.guiLeft + 143, this.guiTop + 50, 18, 18, 194, 0, 18, TEXTURE);
		this.deleteButton = new GuiButtonImage(BUTTON_ID_DELETE, this.guiLeft + 143, this.guiTop + 70, 18, 18, 212, 0, 18, TEXTURE);

		this.buttonPrev = createButton(BUTTON_ID_PREV, this.buttonPrevRectangle, "Previous");
		this.buttonNext = createButton(BUTTON_ID_NEXT, this.buttonNextRectangle, "Next");

		this.addButton(this.buttonPrev);
		this.addButton(this.buttonNext);
		this.addButton(this.saveButton);
		this.addButton(this.loadButton);
		this.addButton(this.deleteButton);

		this.rescanFiles();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		GlStateManager.resetColor();
		drawTexturedModalRect(this.guiLeft - 1, this.guiTop - 1, 0, 0, this.xSize, this.ySize);

		for(int pageOffset = 0; pageOffset < this.itemRectangles.length; ++pageOffset)
		{
			int item = this.pageStart + pageOffset;

			Rectangle itemRectangle = this.itemRectangles[pageOffset];
			if(item >= this.schematics.length)
			{
				itemRectangle.setSize(0, 0);
			}
			else
			{
				itemRectangle.setSize(124, 20);
				itemRectangle.setLocation(8, 10 + 20 * pageOffset);
				itemRectangle.translate(this.guiLeft - 1, this.guiTop - 1);

				boolean selected = this.selected == item;
				ReadableRectangle texture = selected ? LIST_ITEM_SELECTED : LIST_ITEM_UNSELECTED;

				Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
				GlStateManager.color(1F, 1F, 1F, 1F);
				drawTexturedModalRect(itemRectangle.getX(), itemRectangle.getY(), texture.getX(), texture.getY(), texture.getWidth(), texture.getHeight());

				this.drawStringInRectangle(itemRectangle, this.schematics[item].getName());
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.renderHoveredToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		for(int index = 0; index < this.itemRectangles.length; ++index)
		{
			if(this.itemRectangles[index].contains(mouseX, mouseY))
			{
				this.setSelected(this.pageStart + index);
				break;
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		LitematicaBlueprintSerializer serializer = new LitematicaBlueprintSerializer();

		switch(button.id)
		{
		case BUTTON_ID_NEXT:
			this.setPageStart(this.pageStart + this.itemRectangles.length);
			return;
		case BUTTON_ID_PREV:
			this.setPageStart(this.pageStart - this.itemRectangles.length);
			return;
		case BUTTON_ID_LOAD:
			Path src = this.schematics[this.selected].toPath();
			SchematicFileFormat format = SchematicFileFormat.fromPath(src);
			if(format == null)
				return;
			ImhotepMod.instance.getPacketStreamClient().connect("blueprint-encode", (PacketStreamClientChannel channel) -> {
				try(InputStream inputStream = Files.newInputStream(src))
				{
					NBTTagCompound tag = CompressedStreamTools.readCompressed(inputStream);
					tag.setTag(BlueprintTransferHandler.NBT_ARG_TILEPOS, NBTUtil.createPosTag(this.tileEntityBlueprintLibrary.getPos()));
					tag.setString(BlueprintTransferHandler.NBT_ARG_FORMAT, format.name().toLowerCase());
					CompressedStreamTools.writeCompressed(tag, channel.getOutputStream());
					channel.close();
				}
				catch(IOException exc)
				{
					LOGGER.error("Cannot open sample schematic", exc);
				}
			});
			return;
		case BUTTON_ID_SAVE:
			ItemStack stack = this.libraryItemHandler.getStackInSlot(0);
			SchematicBlueprint blueprint = ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT.getSchematic(stack);
			if(blueprint == null)
				return;

			File output = new File(ImhotepMod.instance.getSchematicsDir(), blueprint.getName() + ".litematic");
			try(FileOutputStream fos = new FileOutputStream(output))
			{
				CompressedStreamTools.writeCompressed(serializer.serializeBlueprint(blueprint), fos);
			}
			catch(IOException exc)
			{
				LOGGER.error("Cannot open sample schematic", exc);
			}
			this.rescanFiles();
			break;
		case BUTTON_ID_DELETE:
			this.schematics[this.selected].delete();
			this.rescanFiles();
			break;
		}
		super.actionPerformed(button);
	}

	protected void setSelected(int selected)
	{
		this.selected = selected;
		this.loadButton.enabled = selected != -1;
	}

	protected void setPageStart(int pageStart)
	{
		this.pageStart = pageStart;
		this.buttonNext.enabled = (pageStart + this.itemRectangles.length) < this.schematics.length;
		this.buttonPrev.enabled = (pageStart - this.itemRectangles.length) >= 0;
	}

	private void drawStringInRectangle(ReadableRectangle rectangle, @Nonnull String text)
	{
		int xcenter = rectangle.getX() + rectangle.getWidth() / 2;
		int ycenter = rectangle.getY() + (rectangle.getHeight() - this.fontRenderer.FONT_HEIGHT) / 2 + 1;
		this.drawCenteredString(this.fontRenderer, text, xcenter, ycenter, Color.WHITE.getRGB());
	}

	private GuiButton createButton(int id, ReadableRectangle bounds, String text)
	{
		return new GuiButton(id, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), text);
	}
}
