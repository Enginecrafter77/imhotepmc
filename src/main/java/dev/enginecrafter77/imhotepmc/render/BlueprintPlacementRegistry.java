package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraftforge.fml.common.SidedProxy;

public class BlueprintPlacementRegistry {
	@SidedProxy(modId = ImhotepMod.MOD_ID)
	public static BlueprintPlacementProxy proxy;

	public static interface BlueprintPlacementProxy
	{
		public void registerProvider(BlueprintPlacementProvider placement);
		public void unregisterProvider(BlueprintPlacementProvider placement);
	}

	public static class ClientProxy implements BlueprintPlacementProxy
	{
		@Override
		public void registerProvider(BlueprintPlacementProvider placement)
		{
			RenderBlueprintPlacements.INSTANCE.registerProvider(placement);
		}

		@Override
		public void unregisterProvider(BlueprintPlacementProvider placement)
		{
			RenderBlueprintPlacements.INSTANCE.unregisterProvider(placement);
		}
	}

	public static class ServerProxy implements BlueprintPlacementProxy
	{
		@Override
		public void registerProvider(BlueprintPlacementProvider placement)
		{
			//NOOP
		}

		@Override
		public void unregisterProvider(BlueprintPlacementProvider placement)
		{
			//NOOP
		}
	}
}
