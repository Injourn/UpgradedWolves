package com.example.upgradedwolves.common;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.TrainingHandler;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.capabilities.TrainingHandler.ITraining;
import com.example.upgradedwolves.config.Config;
import com.example.upgradedwolves.entities.goals.WolfFindAndPickUpItemGoal;
import com.example.upgradedwolves.network.PacketHandler;
import com.example.upgradedwolves.network.message.TrainingItemMessage;

import org.apache.logging.log4j.LogManager;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class TrainingEventHandler {
    @SubscribeEvent
    public void BlockBreak(BreakEvent event){        
        LogManager.getLogger().info("Block Broken" + event.getState().getBlock());
        ItemStack foodItem = getFoodStack(event.getPlayer());
        Block block = event.getState().getBlock();
        if(foodItem == null)
            return;
        //Checks if the player broke a block or harvested food
        // if(block instanceof CropsBlock){
        //     ITraining handler = TrainingHandler.getHandler(foodItem);
        //     handler.setAttribute(3);
        //     if(Thread.currentThread().getName() == "Server thread"){                
        //         PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getPlayer()), new TrainingItemMessage(3, event.getPlayer()getId()));
        //     }
        // }
        else if(block instanceof DropExperienceBlock){
            LogManager.getLogger().info("Deep Bug");
            if(Config.COMMON.wolfType.scavengerWolfEnabled.get()){
                ITraining handler = TrainingHandler.getHandler(foodItem);
                handler.setAttribute(2);
                if(Thread.currentThread().getName() == "Server thread"){
                    PacketHandler.INSTANCE.send(new TrainingItemMessage(2, event.getPlayer().getId()),PacketDistributor.PLAYER.with((ServerPlayer)event.getPlayer()));
                }
            }
        }
    }

    @SubscribeEvent
    public void MobKill(LivingDeathEvent event){
        
        if(event.getSource().getDirectEntity() instanceof Player){
            Player player = (Player)event.getSource().getDirectEntity();
            ItemStack foodItem = getFoodStack(player);
            if(foodItem == null)
                return;
            if(event.getEntity() instanceof Monster){
                LogManager.getLogger().info("Killed");
                if(Config.COMMON.wolfType.fighterWolfEnabled.get()){
                    ITraining handler = TrainingHandler.getHandler(foodItem);
                    handler.setAttribute(1);                         
                    if(Thread.currentThread().getName() == "Server thread"){
                        PacketHandler.INSTANCE.send(new TrainingItemMessage(1, player.getId()),PacketDistributor.PLAYER.with((ServerPlayer)player));
                    }
                }
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void VillagerInteract(EntityInteract event){
        
        if(event.getTarget() instanceof Villager){
            LogManager.getLogger().info("Spoke");
            Player player = (Player)event.getEntity();
            ItemStack foodItem = getFoodStack(player);
            if(foodItem == null)
                return;
            if(Config.COMMON.wolfType.showWolfEnabled.get()){
                ITraining handler = TrainingHandler.getHandler(foodItem);
                handler.setAttribute(3);
            }
        }
    }
    
    public static ItemStack getFoodStack(Player player){
        //Checks if the player is holding food in either hand.
        if(player.getMainHandItem().has(DataComponents.FOOD) && player.getMainHandItem().is(ItemTags.MEAT))
            return player.getMainHandItem();
        else if(player.getOffhandItem().has(DataComponents.FOOD) && player.getOffhandItem().is(ItemTags.MEAT))
            return player.getOffhandItem();
        return null;
    }
    public static ItemStack getPlayerHoldingItemStack(Player player, Class<? extends Item> item){
        //Checks if the player is holding food in either hand.
        if(item.isInstance(player.getMainHandItem().getItem()))
            return player.getMainHandItem();
        else if(item.isInstance(player.getOffhandItem().getItem()))
            return player.getOffhandItem();
        return null;
    }

    public static void wolfCollectEntity(Entity entity, Wolf wolf,ItemStack item){
        IWolfStats handler = WolfStatsHandler.getHandler(wolf);        
        int wolfSlot = handler.getInventory().getAvailableSlot(item);
        if(wolfSlot >= 0){
            handler.getInventory().insertItem(wolfSlot, item, false);
            WolfFindAndPickUpItemGoal goal = (WolfFindAndPickUpItemGoal)WolfPlayerInteraction.getWolfGoal(wolf, WolfFindAndPickUpItemGoal.class);
            if(goal != null){
                goal.setEndPoint(wolf.getPosition(0));
            }
            entity.kill();
        }
    }
}