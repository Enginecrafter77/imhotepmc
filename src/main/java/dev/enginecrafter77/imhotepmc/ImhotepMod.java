package dev.enginecrafter77.imhotepmc;

import dev.enginecrafter77.imhotepmc.block.BlockAreaMarker;
import dev.enginecrafter77.imhotepmc.block.BlockBlueprintLibrary;
import dev.enginecrafter77.imhotepmc.block.BlockBuilder;
import dev.enginecrafter77.imhotepmc.blueprint.LitematicaBlueprintSerializer;
import dev.enginecrafter77.imhotepmc.blueprint.translate.BlockRecordCompatTranslationTable;
import dev.enginecrafter77.imhotepmc.entity.EntityConstructionTape;
import dev.enginecrafter77.imhotepmc.gui.ImhotepGUIHandler;
import dev.enginecrafter77.imhotepmc.item.ItemConstructionTape;
import dev.enginecrafter77.imhotepmc.item.ItemSchematicBlueprint;
import dev.enginecrafter77.imhotepmc.net.BlueprintTransferHandler;
import dev.enginecrafter77.imhotepmc.net.MessageBlueprintInscribeHandler;
import dev.enginecrafter77.imhotepmc.net.MessageInscribeBlueprint;
import dev.enginecrafter77.imhotepmc.net.stream.client.PacketStreamDispatcher;
import dev.enginecrafter77.imhotepmc.net.stream.msg.*;
import dev.enginecrafter77.imhotepmc.net.stream.server.PacketStreamManager;
import dev.enginecrafter77.imhotepmc.render.RenderConstructionTape;
import dev.enginecrafter77.imhotepmc.tile.TileEntityAreaMarker;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBlueprintLibrary;
import dev.enginecrafter77.imhotepmc.tile.TileEntityBuilder;
import dev.enginecrafter77.imhotepmc.util.Vec3dSerializer;
import net.minecraft.block.Block;
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
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;

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

    public static BlockAreaMarker BLOCK_AREA_MARKER;
    public static BlockBlueprintLibrary BLOCK_BLUEPRINT_LIBRARY;
    public static BlockBuilder BLOCK_BUILDER;
    public static ItemSchematicBlueprint ITEM_SCHEMATIC_BLUEPRINT;
    public static ItemConstructionTape ITEM_CONSTRUCTION_TAPE;

    private File schematicsDir;
    private SimpleNetworkWrapper netChannel;

    private PacketStreamDispatcher packetStreamClient;
    private PacketStreamManager packetStreamServer;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);

        NetworkRegistry.INSTANCE.registerGuiHandler(ImhotepMod.instance, new ImhotepGUIHandler());
        GameRegistry.registerTileEntity(TileEntityBlueprintLibrary.class, TileEntityBlueprintLibrary.ID);
        GameRegistry.registerTileEntity(TileEntityAreaMarker.class, new ResourceLocation(ImhotepMod.MOD_ID, "area_marker"));
        GameRegistry.registerTileEntity(TileEntityBuilder.class, new ResourceLocation(ImhotepMod.MOD_ID, "builder"));

        this.netChannel = NetworkRegistry.INSTANCE.newSimpleChannel(ImhotepMod.MOD_ID);
        this.netChannel.registerMessage(MessageBlueprintInscribeHandler.class, MessageInscribeBlueprint.class, 0, Side.SERVER);

        this.packetStreamServer = new PacketStreamManager();
        this.packetStreamClient = new PacketStreamDispatcher(this.netChannel, 8192);
        this.netChannel.registerMessage(this.packetStreamServer.getStartHandler(), PacketStreamStartMessage.class, 1, Side.SERVER);
        this.netChannel.registerMessage(this.packetStreamServer.getTransferHandler(), PacketStreamTransferMessage.class, 2, Side.SERVER);
        this.netChannel.registerMessage(this.packetStreamServer.getEndHandler(), PacketStreamEndMessage.class, 3, Side.SERVER);
        this.netChannel.registerMessage(this.packetStreamClient.getStartConfirmHandler(), PacketStreamStartConfirmMessage.class, 4, Side.CLIENT);
        this.netChannel.registerMessage(this.packetStreamClient.getTransferConfimHandler(), PacketStreamTransferConfirmMessage.class, 5, Side.CLIENT);

        this.packetStreamServer.subscribe("blueprint-encode", new BlueprintTransferHandler(new LitematicaBlueprintSerializer(BlockRecordCompatTranslationTable.getInstance()), MessageBlueprintInscribeHandler::onBlueprintReceived));

        BLOCK_BLUEPRINT_LIBRARY = new BlockBlueprintLibrary();
        ITEM_SCHEMATIC_BLUEPRINT = new ItemSchematicBlueprint();
        BLOCK_AREA_MARKER = new BlockAreaMarker();
        ITEM_CONSTRUCTION_TAPE = new ItemConstructionTape();
        BLOCK_BUILDER = new BlockBuilder();

        File configDir = event.getModConfigurationDirectory();
        File gameDirectory = configDir.getParentFile();
        this.schematicsDir = new File(gameDirectory, "schematics");
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onPreInitClient(FMLPreInitializationEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityConstructionTape.class, RenderConstructionTape::new);
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
        return this.packetStreamServer;
    }

    public PacketStreamDispatcher getPacketStreamClient()
    {
        return this.packetStreamClient;
    }

    @SubscribeEvent
    public void registerSerializers(RegistryEvent.Register<DataSerializerEntry> event)
    {
        DataSerializerEntry v3ds = new DataSerializerEntry(Vec3dSerializer.INSTANCE);
        v3ds.setRegistryName(new ResourceLocation(ImhotepMod.MOD_ID, "v3d_serializer"));
        event.getRegistry().register(v3ds);
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event)
    {
        IForgeRegistry<EntityEntry> reg = event.getRegistry();
        reg.register(EntityEntryBuilder.create()
                .entity(EntityConstructionTape.class)
                .id(new ResourceLocation(ImhotepMod.MOD_ID, "construction_tape"), 1)
                .name("construction_tape")
                .tracker(16, 1, false)
                .build());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> reg = event.getRegistry();
        reg.register(ITEM_SCHEMATIC_BLUEPRINT);
        reg.register(ITEM_CONSTRUCTION_TAPE);
        this.registerItemBlock(reg, BLOCK_BLUEPRINT_LIBRARY);
        this.registerItemBlock(reg, BLOCK_AREA_MARKER);
        this.registerItemBlock(reg, BLOCK_BUILDER);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> reg = event.getRegistry();
        reg.register(BLOCK_BLUEPRINT_LIBRARY);
        reg.register(BLOCK_AREA_MARKER);
        reg.register(BLOCK_BUILDER);
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
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_BLUEPRINT_LIBRARY), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "blueprint_library"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_AREA_MARKER), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "area_marker"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_BUILDER), 0, new ModelResourceLocation(new ResourceLocation(ImhotepMod.MOD_ID, "builder"), "inventory"));
    }
}
