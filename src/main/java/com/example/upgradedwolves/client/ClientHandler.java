package com.example.upgradedwolves.client;

import java.util.Collections;
import java.util.List;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.containers.WolfScreen;
import com.example.upgradedwolves.entities.UpgradedWolfModel;
import com.example.upgradedwolves.entities.UpgradedWolfRenderer;
import com.example.upgradedwolves.entities.plushy.CreeperPlushyModel;
import com.example.upgradedwolves.entities.plushy.MobPlushyRenderer;
import com.example.upgradedwolves.entities.plushy.SkeletonPlushyModel;
import com.example.upgradedwolves.entities.plushy.ZombiePlushyModel;
import com.example.upgradedwolves.init.ModContainers;
import com.example.upgradedwolves.init.ModEntities;
import com.example.upgradedwolves.init.ModModelLayers;
import com.example.upgradedwolves.loot_table.init.ModChestLootTable;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = UpgradedWolves.ModId, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {
    public static void setup(){
        
        EntityRenderers.register(EntityType.WOLF, UpgradedWolfRenderer::new);
        EntityRenderers.register(ModEntities.mobPlushyEntityType,MobPlushyRenderer::new);
        EntityRenderers.register(ModEntities.tennisBallEntityType, ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.flyingDiskEntityType, ThrownItemRenderer::new);
        // EntityRenderers.register(ModEntities.tennisBallEntityType,
        //     manager -> new ItemEntityRenderer(manager,Minecraft.getInstance().getItemRenderer()));
        // EntityRenderers.register(ModEntities.flyingDiskEntityType,
        //     manager -> new ItemEntityRenderer(manager,Minecraft.getInstance().getItemRenderer()));

        // EntityRendererManager entityRendererManager = Minecraft.getInstance().getRenderManager();
        // entityRendererManager.register(ModEntities.mobPlushyEntityType, new MobPlushyRenderer(entityRendererManager));

        MenuScreens.register(ModContainers.WOLF_CONTAINER, WolfScreen::new);        
    }

    @SubscribeEvent
    public static void LayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event){        
        event.registerLayerDefinition(ModModelLayers.UPGRADED_WOLF, UpgradedWolfModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ZOMBIE_PLUSH, ZombiePlushyModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CREEPER_PLUSH, CreeperPlushyModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SKELETON_PLUSH, SkeletonPlushyModel::createBodyLayer);
    }
}