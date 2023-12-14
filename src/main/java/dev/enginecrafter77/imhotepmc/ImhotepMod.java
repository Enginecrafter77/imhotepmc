package dev.enginecrafter77.imhotepmc;

import com.google.common.collect.ImmutableMap;
import dev.enginecrafter77.imhotepmc.block.*;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.builder.DefaultBOMProvider;
import dev.enginecrafter77.imhotepmc.blueprint.translate.*;
import dev.enginecrafter77.imhotepmc.cap.AreaMarkJob;
import dev.enginecrafter77.imhotepmc.cap.AreaMarkJobImpl;
import dev.enginecrafter77.imhotepmc.cap.CapabilityAreaMarker;
import dev.enginecrafter77.imhotepmc.gui.ImhotepGUIHandler;
import dev.enginecrafter77.imhotepmc.item.ItemConstructionTape;
import dev.enginecrafter77.imhotepmc.item.ItemSchematicBlueprint;
import dev.enginecrafter77.imhotepmc.item.ItemShapeCard;
import dev.enginecrafter77.imhotepmc.net.*;
import dev.enginecrafter77.imhotepmc.net.stream.PacketStreamWrapper;
import dev.enginecrafter77.imhotepmc.net.stream.client.PacketStreamDispatcher;
import dev.enginecrafter77.imhotepmc.net.stream.server.PacketStreamManager;
import dev.enginecrafter77.imhotepmc.render.*;
import dev.enginecrafter77.imhotepmc.tile.*;
import dev.enginecrafter77.imhotepmc.util.Vec3dSerializer;
import dev.enginecrafter77.imhotepmc.world.AreaMarkDatabase;
import dev.enginecrafter77.imhotepmc.world.sync.WorldDataSyncHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(modid = ImhotepMod.MOD_ID)
public class ImhotepMod {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final int GAME_DATA_VERSION = 1343;

    // Basic mod constants.
    public static final String MOD_ID = "imhotepmc";

    public static CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
        @Nonnull
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(ImhotepMod.ITEM_SCHEMATIC_BLUEPRINT);
        }
    };

    // Make an instance of the mod.
    @Mod.Instance(ImhotepMod.MOD_ID)
    public static ImhotepMod instance;

    public static BlockArchitectTable BLOCK_ARCHITECT_TABLE;
    public static BlockAreaMarker BLOCK_AREA_MARKER;
    public static BlockBlueprintLibrary BLOCK_BLUEPRINT_LIBRARY;
    public static BlockMachineHull BLOCK_MACHINE_HULL;
    public static BlockBuilder BLOCK_BUILDER;
    public static BlockTerraformer BLOCK_TERRAFORMER;
    public static ItemSchematicBlueprint ITEM_SCHEMATIC_BLUEPRINT;
    public static ItemConstructionTape ITEM_CONSTRUCTION_TAPE;
    public static ItemShapeCard ITEM_SHAPE_CARD;

    private DefaultBOMProvider builderBomProvider;
    private BlueprintGameVersionTranslator versionTranslator;

    private File modConfigDir;
    private File schematicsDir;
    private SimpleNetworkWrapper netChannel;

    private PacketStreamWrapper packetStreamer;
    private WorldDataSyncHandler worldDataSyncHandler;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        File configDir = event.getModConfigurationDirectory();
        File gameDirectory = configDir.getParentFile();
        this.modConfigDir = new File(configDir, ImhotepMod.MOD_ID);
        this.schematicsDir = new File(gameDirectory, "schematics");

        MinecraftForge.EVENT_BUS.register(this);

        this.registerTileEntities();
        this.initializeContent();

        this.builderBomProvider = new DefaultBOMProvider();

        BlueprintTranslationBuildEvent translationBuildEvent = new BlueprintTranslationBuildEvent(GAME_DATA_VERSION);
        MinecraftForge.EVENT_BUS.post(translationBuildEvent);
        this.versionTranslator = translationBuildEvent.getBuilder().build();

        this.netChannel = NetworkRegistry.INSTANCE.newSimpleChannel(ImhotepMod.MOD_ID + ":main");
        this.worldDataSyncHandler = WorldDataSyncHandler.create(new ResourceLocation(ImhotepMod.MOD_ID, "worldsync"));
        this.packetStreamer = PacketStreamWrapper.create(new ResourceLocation(ImhotepMod.MOD_ID, "pktstream"), 4096);
        this.packetStreamer.getServerSide().subscribe("blueprint-encode", new BlueprintTransferHandler(new LitematicaBlueprintSerializer(this.versionTranslator)));

        this.netChannel.registerMessage(BlueprintSampleMessageHandler.class, BlueprintSampleMessage.class, 0, Side.SERVER);
        this.netChannel.registerMessage(BuilderDwellUpdateHandler.class, BuilderDwellUpdate.class, 1, Side.CLIENT);
        this.worldDataSyncHandler.register(AreaMarkDatabase.class, ImhotepMod.MOD_ID + ":area_markers");

        NetworkRegistry.INSTANCE.registerGuiHandler(ImhotepMod.instance, new ImhotepGUIHandler());

        CapabilityManager.INSTANCE.register(AreaMarkJob.class, CapabilityAreaMarker.AreaMarkJobStorage.INSTANCE, AreaMarkJobImpl::new);
        MinecraftForge.EVENT_BUS.register(this.worldDataSyncHandler);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event)
    {
        this.builderBomProvider.addOverride(Blocks.STANDING_SIGN, Items.SIGN);
        this.builderBomProvider.addOverride(Blocks.WALL_SIGN, Items.SIGN);
        this.builderBomProvider.addOverride(Blocks.OAK_DOOR, Items.OAK_DOOR);
        this.builderBomProvider.addOverride(Blocks.JUNGLE_DOOR, Items.JUNGLE_DOOR);
        this.builderBomProvider.addOverride(Blocks.DARK_OAK_DOOR, Items.DARK_OAK_DOOR);
        this.builderBomProvider.addOverride(Blocks.ACACIA_DOOR, Items.ACACIA_DOOR);
        this.builderBomProvider.addOverride(Blocks.BIRCH_DOOR, Items.BIRCH_DOOR);
        this.builderBomProvider.addOverride(Blocks.IRON_DOOR, Items.IRON_DOOR);
        this.builderBomProvider.addOverride(Blocks.SPRUCE_DOOR, Items.SPRUCE_DOOR);
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onPreInitClient(FMLPreInitializationEvent event)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArchitectTable.class, new RenderArchitectTable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBuilder.class, new RenderBuilder());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTerraformer.class, new RenderTerraformer());
        RenderWorldAreaMarkers.register();
        TapeLinkingRenderHandler.register();
    }

    public File getSchematicsDir()
    {
        return this.schematicsDir;
    }

    public SimpleNetworkWrapper getNetChannel()
    {
        return this.netChannel;
    }

    public PacketStreamManager getPacketStreamServer()
    {
        return this.packetStreamer.getServerSide();
    }

    public PacketStreamDispatcher getPacketStreamClient()
    {
        return this.packetStreamer.getClientSide();
    }

    public WorldDataSyncHandler getWorldDataSyncHandler()
    {
        return this.worldDataSyncHandler;
    }

    public DefaultBOMProvider getBuilderBomProvider()
    {
        return this.builderBomProvider;
    }

    public BlueprintGameVersionTranslator getVersionTranslator()
    {
        return this.versionTranslator;
    }

    private static final Pattern BTT_FILENAME_PATTERN = Pattern.compile("D([0-9]+).btt");

    @SubscribeEvent
    public void loadTranslationTables(BlueprintTranslationBuildEvent event)
    {
        try
        {
            URL res = ImhotepMod.class.getResource("/external/btt");
            if(res != null)
            {
                URI uri = res.toURI();
                FileSystem fs = FileSystems.newFileSystem(uri, ImmutableMap.of());
                Path path = fs.getPath("/external/btt");
                Files.walk(path).forEach((Path src) -> {
                    try
                    {
                        this.tryLoadTable(event, src);
                    }
                    catch(Exception exc)
                    {
                        LOGGER.error("Error loading translation table from " + src, exc);
                    }
                });
                fs.close();
            }
        }
        catch(Exception exc)
        {
            LOGGER.error("Unable to list blueprint translation tables", exc);
        }

        File bttDir = new File(this.modConfigDir, "btt");
        if(bttDir.exists())
        {
            for(File file : Objects.requireNonNull(bttDir.listFiles()))
            {
                Path path = file.toPath();

                try
                {
                    this.tryLoadTable(event, path);
                }
                catch(Exception exc)
                {
                    LOGGER.error("Unable to load blueprint translation table " + file.getName(), exc);
                }
            }
		}
    }

    private void tryLoadTable(BlueprintTranslationBuildEvent event, Path path) throws IOException, MalformedTranslationRuleException
    {
        Matcher matcher = BTT_FILENAME_PATTERN.matcher(path.getFileName().toString());
        if(!matcher.matches())
            return;
        int versionFrom = Integer.parseInt(matcher.group(1));

        try(InputStream is = Files.newInputStream(path))
        {
            BlueprintTranslationRuleCompiler compiler = new BlueprintTranslationRuleCompiler();
            LOGGER.info("Loading blueprint translation table " + path.getFileName());
            compiler.append(is);
            event.getBuilder().setTable(versionFrom, compiler.compile());
        }
    }

    public void initializeContent()
    {
        BLOCK_ARCHITECT_TABLE = new BlockArchitectTable();
        BLOCK_BLUEPRINT_LIBRARY = new BlockBlueprintLibrary();
        ITEM_SCHEMATIC_BLUEPRINT = new ItemSchematicBlueprint();
        BLOCK_AREA_MARKER = new BlockAreaMarker();
        ITEM_CONSTRUCTION_TAPE = new ItemConstructionTape();
        BLOCK_BUILDER = new BlockBuilder();
        BLOCK_TERRAFORMER = new BlockTerraformer();
        ITEM_SHAPE_CARD = new ItemShapeCard();
        BLOCK_MACHINE_HULL = new BlockMachineHull();
    }

    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityBlueprintLibrary.class, TileEntityBlueprintLibrary.ID);
        GameRegistry.registerTileEntity(TileEntityAreaMarker.class, new ResourceLocation(ImhotepMod.MOD_ID, "area_marker"));
        GameRegistry.registerTileEntity(TileEntityBuilder.class, new ResourceLocation(ImhotepMod.MOD_ID, "builder"));
        GameRegistry.registerTileEntity(TileEntityArchitectTable.class, new ResourceLocation(ImhotepMod.MOD_ID, "architect_table"));
        GameRegistry.registerTileEntity(TileEntityTerraformer.class, new ResourceLocation(ImhotepMod.MOD_ID, "terraformer"));
    }

    @SubscribeEvent
    public void registerSerializers(RegistryEvent.Register<DataSerializerEntry> event)
    {
        DataSerializerEntry v3ds = new DataSerializerEntry(Vec3dSerializer.INSTANCE);
        v3ds.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "v3d_serializer"));
        event.getRegistry().register(v3ds);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> reg = event.getRegistry();
        reg.register(ITEM_SCHEMATIC_BLUEPRINT);
        reg.register(ITEM_CONSTRUCTION_TAPE);
        reg.register(ITEM_SHAPE_CARD);
        this.registerItemBlock(reg, BLOCK_BLUEPRINT_LIBRARY);
        this.registerItemBlock(reg, BLOCK_AREA_MARKER);
        this.registerItemBlock(reg, BLOCK_BUILDER);
        this.registerItemBlock(reg, BLOCK_ARCHITECT_TABLE);
        this.registerItemBlock(reg, BLOCK_TERRAFORMER);
        this.registerItemBlock(reg, BLOCK_MACHINE_HULL);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> reg = event.getRegistry();
        reg.register(BLOCK_BLUEPRINT_LIBRARY);
        reg.register(BLOCK_AREA_MARKER);
        reg.register(BLOCK_BUILDER);
        reg.register(BLOCK_ARCHITECT_TABLE);
        reg.register(BLOCK_TERRAFORMER);
        reg.register(BLOCK_MACHINE_HULL);
    }

    private void registerItemBlock(IForgeRegistry<Item> reg, Block block)
    {
        ResourceLocation name = block.getRegistryName();
        if(name == null)
            throw new NullPointerException("Block has no registry name set!");

        Item item = new ItemBlock(block);
        item.setRegistryName(block.getRegistryName());
        reg.register(item);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(ITEM_SCHEMATIC_BLUEPRINT, ItemSchematicBlueprint.META_EMPTY, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "schematic_blueprint_empty"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_SCHEMATIC_BLUEPRINT, ItemSchematicBlueprint.META_WRITTEN, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "schematic_blueprint_written"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_CONSTRUCTION_TAPE, 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "construction_tape"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_SHAPE_CARD, TerraformMode.FILL.ordinal(), new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "shape_card_fill"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_SHAPE_CARD, TerraformMode.CLEAR.ordinal(), new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "shape_card_clear"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_SHAPE_CARD, TerraformMode.ELLIPSOID.ordinal(), new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "shape_card_ellipsoid"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_SHAPE_CARD, TerraformMode.PYRAMID.ordinal(), new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "shape_card_pyramid"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_SHAPE_CARD, TerraformMode.DOME.ordinal(), new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "shape_card_dome"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_BLUEPRINT_LIBRARY), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "blueprint_library"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_AREA_MARKER), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "area_marker"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_BUILDER), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "builder"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_ARCHITECT_TABLE), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "architect_table"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_TERRAFORMER), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "terraformer"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_MACHINE_HULL), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "machine_hull"), "inventory"));
    }
}
