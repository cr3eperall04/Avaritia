package com.cr3eperall.avaritiaalpha;

import com.cr3eperall.avaritiaalpha.blocks.CrystalMatrixBlock;
import com.cr3eperall.avaritiaalpha.blocks.ModBlocks;
import com.cr3eperall.avaritiaalpha.blocks.NeutronCollector;
import com.cr3eperall.avaritiaalpha.blocks.NeutroniumBlock;
import com.cr3eperall.avaritiaalpha.blocks.gui.NeutronCollectorContainer;
import com.cr3eperall.avaritiaalpha.blocks.tiles.NeutronCollectorTile;
import com.cr3eperall.avaritiaalpha.config.Config;
import com.cr3eperall.avaritiaalpha.entity.HeavenArrowEntity;
import com.cr3eperall.avaritiaalpha.entity.HeavenSubArrowEntity;
import com.cr3eperall.avaritiaalpha.entity.ModEntities;
import com.cr3eperall.avaritiaalpha.items.*;
import com.cr3eperall.avaritiaalpha.items.tools.*;
import com.cr3eperall.avaritiaalpha.setup.ClientProxy;
import com.cr3eperall.avaritiaalpha.setup.IProxy;
import com.cr3eperall.avaritiaalpha.setup.ModSetup;
import com.cr3eperall.avaritiaalpha.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(AvaritiaAlpha.MOD_ID)
public class AvaritiaAlpha {

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public static final String MOD_ID="avaritiaalpha";

    public static ModSetup setup = new ModSetup();
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public AvaritiaAlpha() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        //Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("avaritiaalpha-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("avaritiaalpha-common.toml"));




        // Register the enqueueIMC method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        //MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();
        proxy.init();
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            IForgeRegistry registry=blockRegistryEvent.getRegistry();
            registry.register(new CrystalMatrixBlock());
            registry.register(new NeutronCollector());
            registry.register(new NeutroniumBlock());

        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            IForgeRegistry registry=itemRegistryEvent.getRegistry();
            registry.register(new BlockItem(ModBlocks.CRYSTALMATRIXBLOCK, new Item.Properties().group(setup.itemGroup)).setRegistryName("crystal_matrix_block"));
            registry.register(new BlockItem(ModBlocks.NEUTRONCOLLECTOR, new Item.Properties().group(setup.itemGroup)).setRegistryName("neutron_collector"));
            registry.register(new BlockItem(ModBlocks.NEUTRONIUMBLOCK,new Item.Properties().group(setup.itemGroup)).setRegistryName("neutronium_block"));
            registry.register(new DiamondLattice());
            registry.register(new CrystalMatrixIngot());
            registry.register(new NeutroniumPile());
            registry.register(new NeutroniumNugget());
            registry.register(new NeutroniumIngot());
            registry.register(new InfinityAxe());
            registry.register(new InfinityBow());
            registry.register(new MatterCluster());
            registry.register(new InfinitySword());
            registry.register(new InfinityHoe());
            registry.register(new InfinityPickaxe());
        }

        @SubscribeEvent
        public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> entityRegistryEvent) {
            entityRegistryEvent.getRegistry().register(ModEntities.heavenArrowEntityEntityType);
            entityRegistryEvent.getRegistry().register(ModEntities.heavenSubArrowEntityEntityType);
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> tileEntityRegistryEvent) {
            tileEntityRegistryEvent.getRegistry().register(TileEntityType.Builder.create(NeutronCollectorTile::new,ModBlocks.NEUTRONCOLLECTOR).build(null).setRegistryName("neutron_collector"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> containerRegistryEvent) {
            containerRegistryEvent.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                return new NeutronCollectorContainer(windowId, proxy.getClientWorld(), data.readBlockPos(),inv, proxy.getClientPlayer());
            }).setRegistryName("neutron_collector"));
        }
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }


}
