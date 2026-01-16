package dev.enginecrafter77.imhotepmc.render;

import dev.enginecrafter77.imhotepmc.ImhotepMod;
import net.minecraftforge.fml.common.SidedProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlueprintPlacementRegistry {
	static final Logger LOGGER = LogManager.getLogger(BlueprintPlacementRegistry.class);

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
			LOGGER.info("Registering BlueprintPlacementProvider " + placement + " on client side");
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
