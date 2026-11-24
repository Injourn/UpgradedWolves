package com.example.upgradedwolves.loot_table;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.upgradedwolves.UpgradedWolves;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class LootLoader {
    List<BasicLootModel> lootModelList;

    public LootLoader(ResourceLocation resource){
        try{
            Resource iresource = (Resource) Minecraft.getInstance().getResourceManager().getResource(resource).get();
            Gson lootData =  new Gson();
            Type listOfMyClassObject = new TypeToken<ArrayList<BasicLootModel>>() {}.getType();
            lootModelList = lootData.fromJson( new InputStreamReader(iresource.open()),listOfMyClassObject);            
        } catch (IOException e) {
            LogManager.getLogger().error("Failed to load loot table " + e.getMessage() );
        }
    }

    public LootLoader(String tableName){
        this(UpgradedWolves.getId("loot_tables/" + tableName + ".json"));
    }

    public ItemStack getRandomItem(){
        Random r = new Random();
        int rand = r.nextInt(100);
        int bound = 0;
        for(BasicLootModel b : lootModelList){
            bound += b.chance;
            if(rand < bound){
                String itemId = b.items.get(r.nextInt(b.items.size()));
                Item selectedItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemId));
                return new ItemStack(selectedItem);
            }
        }
        LogManager.getLogger().error("This shouldn't happen... you know, I keep saying that, and it rarely does ever happen when I do it correctly, but sheesh. You gotta wonder if some magic thing were to happen to cause a user to see this erroneously or something like this and never be believed when they try to explain that it happened to them. It's bizarre isn't it?");
        return null;
    }
}
