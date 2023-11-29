package dev.enginecrafter77.imhotepmc.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.util.ReadableRectangle;

import javax.annotation.Nonnull;

public class GuiUtil {
	public static void drawStringInRectangle(Gui in, FontRenderer fontRenderer, ReadableRectangle rectangle, @Nonnull String text, int color)
	{
		int xcenter = rectangle.getX() + rectangle.getWidth() / 2;
		int ycenter = rectangle.getY() + (rectangle.getHeight() - fontRenderer.FONT_HEIGHT) / 2 + 1;
		in.drawCenteredString(fontRenderer, text, xcenter, ycenter, color);
	}
}
