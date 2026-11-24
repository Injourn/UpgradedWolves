package com.example.upgradedwolves.init;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.upgradedwolves.loot_table.init.ModChestLootTable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.core.RegistryAccess;

public class ModChestLoot{

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event){
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        dataGenerator.addProvider(event.includeServer(),
            new LootTableProvider(
                packOutput,
                Collections.EMPTY_SET,
                List.of(new LootTableProvider.SubProviderEntry(lookup -> new ModChestLootTable(),LootContextParamSets.CHEST)),
                CompletableFuture.completedFuture(RegistryAccess.EMPTY)));
    }
}
