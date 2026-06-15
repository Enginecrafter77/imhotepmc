package dev.enginecrafter77.imhotepmc;

import dev.enginecrafter77.imhotepmc.render.BlueprintPlacementProvider;
import dev.enginecrafter77.imhotepmc.render.RenderBlueprintPlacements;
import net.minecraftforge.fml.common.SidedProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImhotepProxy {
	static final Logger LOGGER = LogManager.getLogger(ImhotepProxy.class);

	@SidedProxy(modId = ImhotepMod.MOD_ID)
	public static Proxy INSTANCE;

	public static interface Proxy
	{
		public void registerBlueprintPlacementProvider(BlueprintPlacementProvider placement);
		public void unregisterBlueprintPlacementProvider(BlueprintPlacementProvider placement);
	}

	public static class ClientProxy implements Proxy
	{
		@Override
		public void registerBlueprintPlacementProvider(BlueprintPlacementProvider placement)
		{
			LOGGER.info("Registering BlueprintPlacementProvider {} on client side", placement);
			RenderBlueprintPlacements.INSTANCE.registerProvider(placement);
		}

		@Override
		public void unregisterBlueprintPlacementProvider(BlueprintPlacementProvider placement)
		{
			RenderBlueprintPlacements.INSTANCE.unregisterProvider(placement);
		}
	}

	public static class ServerProxy implements Proxy
	{
		@Override
		public void registerBlueprintPlacementProvider(BlueprintPlacementProvider placement)
		{
			//NOOP
		}

		@Override
		public void unregisterBlueprintPlacementProvider(BlueprintPlacementProvider placement)
		{
			//NOOP
		}
	}
}
