package dev.enginecrafter77.imhotepmc;

import dev.enginecrafter77.imhotepmc.block.*;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslation;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlueprintTranslationRuleCompiler;
import dev.enginecrafter77.imhotepmc.blueprint.translate.MalformedTranslationRuleException;
import dev.enginecrafter77.imhotepmc.cap.CapabilityAreaMarker;
import dev.enginecrafter77.imhotepmc.gui.ImhotepGUIHandler;
import dev.enginecrafter77.imhotepmc.item.ItemConstructionTape;
import dev.enginecrafter77.imhotepmc.item.ItemSchematicBlueprint;
import dev.enginecrafter77.imhotepmc.item.ItemShapeCard;
import dev.enginecrafter77.imhotepmc.net.BlueprintSampleMessage;
import dev.enginecrafter77.imhotepmc.net.BlueprintSampleMessageHandler;
import dev.enginecrafter77.imhotepmc.net.BlueprintTransferHandler;
import dev.enginecrafter77.imhotepmc.net.stream.PacketStreamWrapper;
import dev.enginecrafter77.imhotepmc.net.stream.client.PacketStreamDispatcher;
import dev.enginecrafter77.imhotepmc.net.stream.server.PacketStreamManager;
import dev.enginecrafter77.imhotepmc.render.RenderArchitectTable;
import dev.enginecrafter77.imhotepmc.render.RenderBuilder;
import dev.enginecrafter77.imhotepmc.render.RenderTerraformer;
import dev.enginecrafter77.imhotepmc.render.RenderWorldAreaMarkers;
import dev.enginecrafter77.imhotepmc.tile.*;
import dev.enginecrafter77.imhotepmc.util.Vec3dSerializer;
import dev.enginecrafter77.imhotepmc.world.AreaMarkDatabase;
import dev.enginecrafter77.imhotepmc.world.sync.WorldDataSyncHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
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
import java.io.IOException;
import java.io.InputStream;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(modid = ImhotepMod.MOD_ID)
public class ImhotepMod {
    private static final Logger LOGGER = LogManager.getLogger();

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
    public static BlockBuilder BLOCK_BUILDER;
    public static BlockTerraformer BLOCK_TERRAFORMER;
    public static ItemSchematicBlueprint ITEM_SCHEMATIC_BLUEPRINT;
    public static ItemConstructionTape ITEM_CONSTRUCTION_TAPE;
    public static ItemShapeCard ITEM_SHAPE_CARD;

    private File schematicsDir;
    private SimpleNetworkWrapper netChannel;

    private PacketStreamWrapper packetStreamer;
    private WorldDataSyncHandler worldDataSyncHandler;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);

        NetworkRegistry.INSTANCE.registerGuiHandler(ImhotepMod.instance, new ImhotepGUIHandler());
        GameRegistry.registerTileEntity(TileEntityBlueprintLibrary.class, TileEntityBlueprintLibrary.ID);
        GameRegistry.registerTileEntity(TileEntityAreaMarker.class, new ResourceLocation(ImhotepMod.MOD_ID, "area_marker"));
        GameRegistry.registerTileEntity(TileEntityBuilder.class, new ResourceLocation(ImhotepMod.MOD_ID, "builder"));
        GameRegistry.registerTileEntity(TileEntityArchitectTable.class, new ResourceLocation(ImhotepMod.MOD_ID, "architect_table"));
        GameRegistry.registerTileEntity(TileEntityTerraformer.class, new ResourceLocation(ImhotepMod.MOD_ID, "terraformer"));

        BlueprintTranslation translation = BlueprintTranslation.pass();
        try
        {
            ResourceLocation res = new ResourceLocation(ImhotepMod.MOD_ID, "btt/test.btt");
            InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream();
            BlueprintTranslationRuleCompiler compiler = new BlueprintTranslationRuleCompiler(in);
            translation = compiler.compile();
            in.close();
        }
        catch(IOException | MalformedTranslationRuleException exc)
        {
            exc.printStackTrace();
        }

        this.netChannel = NetworkRegistry.INSTANCE.newSimpleChannel(ImhotepMod.MOD_ID + ":main");
        this.worldDataSyncHandler = WorldDataSyncHandler.create(new ResourceLocation(ImhotepMod.MOD_ID, "world_data_sync"));
        this.packetStreamer = PacketStreamWrapper.create(new ResourceLocation(ImhotepMod.MOD_ID, "packet_stream"), 8192);
        this.packetStreamer.getServerSide().subscribe("blueprint-encode", new BlueprintTransferHandler(new LitematicaBlueprintSerializer(translation)));

        this.netChannel.registerMessage(BlueprintSampleMessageHandler.class, BlueprintSampleMessage.class, 0, Side.SERVER);

        BLOCK_ARCHITECT_TABLE = new BlockArchitectTable();
        BLOCK_BLUEPRINT_LIBRARY = new BlockBlueprintLibrary();
        ITEM_SCHEMATIC_BLUEPRINT = new ItemSchematicBlueprint();
        BLOCK_AREA_MARKER = new BlockAreaMarker();
        ITEM_CONSTRUCTION_TAPE = new ItemConstructionTape();
        BLOCK_BUILDER = new BlockBuilder();
        BLOCK_TERRAFORMER = new BlockTerraformer();
        ITEM_SHAPE_CARD = new ItemShapeCard();

        this.worldDataSyncHandler.register(AreaMarkDatabase.class, ImhotepMod.MOD_ID + ":area_markers");

        CapabilityAreaMarker.register();
        MinecraftForge.EVENT_BUS.register(this.worldDataSyncHandler);

        File configDir = event.getModConfigurationDirectory();
        File gameDirectory = configDir.getParentFile();
        this.schematicsDir = new File(gameDirectory, "schematics");
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onPreInitClient(FMLPreInitializationEvent event)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArchitectTable.class, new RenderArchitectTable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBuilder.class, new RenderBuilder());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTerraformer.class, new RenderTerraformer());
        RenderWorldAreaMarkers.register();
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
    }
}
