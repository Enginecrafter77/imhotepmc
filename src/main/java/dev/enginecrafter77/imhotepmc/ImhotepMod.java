package dev.enginecrafter77.imhotepmc;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(modid = ImhotepMod.MOD_ID, dependencies = "required-after:crafttweaker")
public class ImhotepMod {
    private static final Logger LOGGER = LogManager.getLogger();

    // Basic mod constants.
    public static final String MOD_ID = "morbtweaker";

    // Make an instance of the mod.
    @Mod.Instance(ImhotepMod.MOD_ID)
    public static ImhotepMod instance;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void onAllModsLoaded(FMLLoadCompleteEvent event)
    {

    }
}
