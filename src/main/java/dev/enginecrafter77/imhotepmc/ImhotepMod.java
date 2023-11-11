package dev.enginecrafter77.imhotepmc;

import dev.enginecrafter77.imhotepmc.blueprint.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(modid = ImhotepMod.MOD_ID)
public class ImhotepMod {
    private static final Logger LOGGER = LogManager.getLogger();

    // Basic mod constants.
    public static final String MOD_ID = "imhotepmc";

    // Make an instance of the mod.
    @Mod.Instance(ImhotepMod.MOD_ID)
    public static ImhotepMod instance;

    private File schematicsDir;
    private StructureBlueprint sampleSchamatic;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);

        File configDir = event.getModConfigurationDirectory();
        File gameDirectory = configDir.getParentFile();
        this.schematicsDir = new File(gameDirectory, "schematics");
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        File schematicFile = new File(this.schematicsDir, "sample.litematic");

        NBTBlueprintSerializer serializer = new LitematicaBlueprintSerializer(BlockRecordCompatTranslationTable.getInstance());
        try(InputStream inputStream = Files.newInputStream(schematicFile.toPath()))
        {
            NBTTagCompound tag = CompressedStreamTools.readCompressed(inputStream);
            this.sampleSchamatic = serializer.deserializeBlueprint(tag);
        }
        catch(IOException exc)
        {
            LOGGER.error("Cannot open sample schematic", exc);
        }
    }

    @SubscribeEvent
    public void onItemUsedEvent(PlayerInteractEvent.RightClickBlock event)
    {
        if(this.sampleSchamatic == null)
        {
            LOGGER.error("Sample schematic not loaded!");
            return;
        }

        ItemStack stack = event.getItemStack();
        if(stack.getItem() != Items.STICK)
            return;

        World world = event.getWorld();
        BlockPos start = event.getPos().up();
        for(Map.Entry<Vec3i, ResolvedBlueprintBlock> entry : sampleSchamatic.getStructureBlocks().entrySet())
        {
            BlockPos dest = start.add(entry.getKey());
            ResolvedBlueprintBlock data = entry.getValue();
            IBlockState state = data.getBlockState();

            world.setBlockState(dest, state, 2);

            TileEntity tile = data.createTileEntity(world);
            if(tile != null)
                world.setTileEntity(dest, tile);

            world.scheduleBlockUpdate(dest, state.getBlock(), 100, 1);
        }
    }
}
