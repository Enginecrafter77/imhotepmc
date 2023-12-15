package dev.enginecrafter77.imhotepmc.render;

import com.google.common.collect.Maps;
import dev.enginecrafter77.imhotepmc.blueprint.BlueprintPlacement;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class RenderBlueprintPlacements {
	public static final RenderBlueprintPlacements INSTANCE = new RenderBlueprintPlacements();

	private final Map<Long, PlacementRenderHolder> registry;

	public RenderBlueprintPlacements()
	{
		this.registry = Maps.newHashMap();
	}

	public void registerProvider(BlueprintPlacementProvider placement)
	{
		this.registry.put(placement.getPlacementProviderUniqueId(), new PlacementRenderHolder(placement));
	}

	public void unregisterProvider(BlueprintPlacementProvider placement)
	{
		this.registry.remove(placement.getPlacementProviderUniqueId());
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{
		// Cull inactive providers
		this.registry.values().removeIf(PlacementRenderHolder::shouldBeCulled);
		for(PlacementRenderHolder holder : this.registry.values())
			holder.doRender(event.getPartialTicks());
	}

	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	private static class PlacementRenderHolder implements IAutoRenderable
	{
		private final BlueprintPlacementProvider provider;
		private final RenderBlueprintPlacement render;

		public PlacementRenderHolder(BlueprintPlacementProvider provider)
		{
			this.render = new RenderBlueprintPlacement();
			this.provider = provider;
		}

		public boolean shouldBeCulled()
		{
			return this.provider.isInvalid();
		}

		@Override
		public void doRender(float partialTicks)
		{
			if(!this.provider.isPlacementVisible())
				return;

			this.render.setPlacement(this.provider.getPlacement());
			this.render.doRender(partialTicks);
		}
	}
}
