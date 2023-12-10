package dev.enginecrafter77.imhotepmc.render;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TapeLinkingRenderHandler {
	private static TapeLinkingRenderHandler instance = null;

	private final RenderPlayerTapeLinking render;

	public TapeLinkingRenderHandler()
	{
		this.render = new RenderPlayerTapeLinking();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{
		this.render.render(event);
	}

	public RenderPlayerTapeLinking getRender()
	{
		return this.render;
	}

	public static TapeLinkingRenderHandler getInstance()
	{
		if(instance == null)
			instance = new TapeLinkingRenderHandler();
		return instance;
	}

	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(getInstance());
	}
}
