package com.example.upgradedwolves;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.stream.Collectors;

import com.example.upgradedwolves.capabilities.TrainingHandler;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.client.ClientHandler;
import com.example.upgradedwolves.common.DamageHandler;
import com.example.upgradedwolves.common.TrainingEventHandler;
import com.example.upgradedwolves.common.WolfPlayerInteraction;
import com.example.upgradedwolves.init.ModChestLoot;
import com.example.upgradedwolves.init.ModContainers;
import com.example.upgradedwolves.init.ModEntities;
import com.example.upgradedwolves.itemHandler.WolfToysHandler;
import com.example.upgradedwolves.network.PacketHandler;
import com.example.upgradedwolves.personality.WolfPersonality;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(UpgradedWolves.ModId)
public class UpgradedWolves
{

    public static final String ModId = "upgradedwolves";
    // Directly reference a log4j logger.    
    public static final Logger LOGGER = LogManager.getLogger();

    public UpgradedWolves() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Setup Events from DamageHandler.java
        MinecraftForge.EVENT_BUS.register(new DamageHandler());
        MinecraftForge.EVENT_BUS.register(new WolfPlayerInteraction());
        MinecraftForge.EVENT_BUS.register(new TrainingEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModChestLoot());
        //Future reference for static registering...
        FMLJavaModLoadingContext.get().getModEventBus().register(ModContainers.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(WolfToysHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(ModEntities.class);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        PacketHandler.register();

        WolfStatsHandler.register();
        TrainingHandler.register();
        WolfPersonality.addGoals();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client                    
        ClientHandler.setup();    
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("upgradedwolves", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

    public static boolean isDevBuild(){
        String version = getVersion();
        return "NONE".equals(version);
    }
    public static ResourceLocation getId(String path){
        return new ResourceLocation(ModId,path);
    }
    public static String getVersion(){
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(ModId);
        if(o.isPresent()){
            return o.get().getModInfo().getVersion().toString();
        }
        return "NONE";
    }
}
