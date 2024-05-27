package com.example.upgradedwolves.common;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.TrainingHandler;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.capabilities.WolfType;
import com.example.upgradedwolves.capabilities.TrainingHandler.ITraining;
import com.example.upgradedwolves.config.Config;
import com.example.upgradedwolves.containers.ContainerProviderWolfInventory;
import com.example.upgradedwolves.entities.goals.FollowOwnerVariableGoal;
import com.example.upgradedwolves.entities.goals.TugOfWarGaol;
import com.example.upgradedwolves.entities.goals.WolfBiasRoamGoal;
import com.example.upgradedwolves.entities.utilities.EntityFinder;
import com.example.upgradedwolves.itemHandler.WolfItemStackHandler;
import com.example.upgradedwolves.items.TugOfWarRopeItem;
import com.example.upgradedwolves.items.GoldenBone.GoldenBoneAbstract;
import com.example.upgradedwolves.network.PacketHandler;
import com.example.upgradedwolves.network.message.RenderMessage;
import com.example.upgradedwolves.items.MobPlushy;

import org.apache.logging.log4j.LogManager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeConfig.Common;
import net.minecraftforge.common.extensions.IForgeServerPlayer;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class WolfPlayerInteraction {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityInteract(final EntityInteract event){
        //LogManager.getLogger().info(event.getEntity().toString());
        if(event.getTarget() instanceof Wolf){
            final Wolf wolf = (Wolf) event.getTarget();            
            final IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            if(handler.getTugOfWarStatus()){
                wolf.setOrderedToSit(false);
                return;
            }
            if(wolf.getOwner() == event.getEntity() && event.getEntity().isCrouching()){
                if(Thread.currentThread().getName() == "Server thread"){
                    MenuProvider wolfInventory = new ContainerProviderWolfInventory(wolf,handler.getInventory());
                    CompoundTag nbt = new CompoundTag();
                    final IForgeServerPlayer player = (IForgeServerPlayer)event.getEntity();
                    nbt.putInt("strLevel", handler.getLevel(WolfStatsEnum.Strength));                    
                    nbt.putInt("spdLevel", handler.getLevel(WolfStatsEnum.Speed));
                    nbt.putInt("intLevel", handler.getLevel(WolfStatsEnum.Intelligence));
                    nbt.putFloat("strNum", handler.getStatRatio(WolfStatsEnum.Strength));
                    nbt.putFloat("spdNum", handler.getStatRatio(WolfStatsEnum.Speed));
                    nbt.putFloat("intNum", handler.getStatRatio(WolfStatsEnum.Intelligence));
                    nbt.putInt("wolfType", handler.getWolfType());
                    player.openMenu(
                        wolfInventory,
                        (packetBuffer) ->{
                            packetBuffer.writeInt(handler.getInventory().getSlots());
                            packetBuffer.writeInt(wolf.getId());
                            packetBuffer.writeNbt(nbt);
                        });
                }
                wolf.setOrderedToSit(!wolf.isOrderedToSit());
            }
            else{
                LogManager.getLogger().info(handler.getLevel(WolfStatsEnum.Love));
                LogManager.getLogger().info(handler.getWolfType());
                LogManager.getLogger().info(handler.getWolfPersonality().getClass().getName());
                handler.InitLove();       
                final ItemStack foodItem = TrainingEventHandler.getFoodStack(event.getEntity());
                final ItemStack goldenBoneItem = TrainingEventHandler.getPlayerHoldingItemStack(event.getEntity(), GoldenBoneAbstract.class);
                final ItemStack tugOfWarRopeItem = TrainingEventHandler.getPlayerHoldingItemStack(event.getEntity(), TugOfWarRopeItem.class);                
                if(foodItem != null){
                    final ITraining tHandler = TrainingHandler.getHandler(foodItem);
                    final int item = tHandler.getAttribute();                
                    if(item == 0)
                        return;
                    else{
                        handler.setWolfType(item);
                        handler.setWolffur(wolf.level().random.nextInt(3));
                        handler.addGoals();
                        handler.handleWolfGoals();
                        foodItem.shrink(1);
                        tHandler.resetAttribute();
                        if(Thread.currentThread().getName() == "Server thread")
                            PacketHandler.INSTANCE.send(new RenderMessage( wolf.getId(),WolfStatsHandler.getHandler(wolf).getWolfType(),handler.getWolfFur()),PacketDistributor.TRACKING_ENTITY.with(wolf));
                    }
                } else if (goldenBoneItem != null){
                    GoldenBoneAbstract goldenBone = (GoldenBoneAbstract)goldenBoneItem.getItem();
                    if(Thread.currentThread().getName() == "Server thread")
                        goldenBone.rightClickWolf(wolf,handler);
                    if(!event.getEntity().isCreative())
                        goldenBoneItem.shrink(1);
                    wolf.setOrderedToSit(!wolf.isOrderedToSit());
                } else if (tugOfWarRopeItem != null){
                    handler.setRopeHolder(event.getEntity());
                    wolf.setOrderedToSit(true);
                    tugOfWarRopeItem.shrink(1);
                }
                if(handler.getWolfType() ==  3){
                    if(handler.getRoamPoint() == null){
                        handler.setRoamPoint(wolf.getPosition(1));
                    } else {
                        handler.setRoamPoint(null);
                    }
                    wolf.setOrderedToSit(true);
                }
                else{
                    handler.setRoamPoint(null);
                }
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onEntitySpawn(MobSpawnEvent event) {
        if(event.getEntity() instanceof Wolf){                
            Wolf wolf = (Wolf)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            wolf.setCanPickUpLoot(true);
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(handler.getWolfSpeed());
        }        
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onStartTracking(PlayerEvent.StartTracking event){        
        event.getTarget().getCapability(WolfStatsHandler.CAPABILITY_WOLF_STATS).ifPresent(capability -> {
            Wolf wolf = (Wolf)event.getTarget();
            PacketHandler.INSTANCE.send(new RenderMessage(wolf.getId(),capability.getWolfType(),WolfStatsHandler.getHandler(wolf).getWolfFur()),PacketDistributor.PLAYER.with((ServerPlayer)event.getEntity()));
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(capability.getWolfSpeed());
        });
    }

    @SubscribeEvent
    public void onWolfJump(LivingJumpEvent event){
        if(event.getEntity() instanceof Wolf){
            Wolf wolf = (Wolf)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            //Scavenger Wolf Bonus
            handler.addXp(WolfStatsEnum.Speed,(handler.getWolfType() == 2 ? Config.COMMON.wolfLevelling.scavengerWolfSpeedXp.get() : 0));
            PacketHandler.INSTANCE.send(new RenderMessage( wolf.getId(),handler.getWolfType(),handler.getWolfFur()), PacketDistributor.TRACKING_ENTITY.with(wolf));
        }
    }
    @SubscribeEvent
    public void onWolfDestroyBlock(LivingDestroyBlockEvent event){
        if(event.getEntity() instanceof Wolf){
            Wolf wolf = (Wolf)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            handler.addXp(WolfStatsEnum.Strength,1);
            handler.addXp(WolfStatsEnum.Intelligence,(handler.getWolfType() == 3 ? 2 : 1));
            PacketHandler.INSTANCE.send(new RenderMessage( wolf.getId(),handler.getWolfType(),handler.getWolfFur()), PacketDistributor.TRACKING_ENTITY.with(wolf));
        }
    }
    @SubscribeEvent
    public void onWolfPickUp(LivingTickEvent event){        
        if(event.getEntity() instanceof Wolf){
            Wolf wolf = (Wolf)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            WolfItemStackHandler wolfInventory = handler.getInventory();

            Optional<WrappedGoal> optGoal = wolf.goalSelector.getRunningGoals().filter((goal) -> {
                return goal.getGoal().getClass() == FollowOwnerGoal.class;
            }).findFirst();
            if(optGoal.isPresent()){
                wolf.goalSelector.removeGoal(optGoal.get().getGoal());
            }
            if(handler.getWolfType() == 3)
                removeWolfGoal(wolf, WaterAvoidingRandomStrollGoal.class);

            wolf.setCanPickUpLoot(false);
            for(ItemEntity itementity : wolf.level().getEntitiesOfClass(ItemEntity.class, wolf.getBoundingBox().inflate(1.0D, 0.0D, 1.0D))) {
                if (wolfInventory.getAvailableSlot(itementity.getItem()) >= 0) {
                    wolf.setCanPickUpLoot(true);
                }
            }

            if(wolf.getMainHandItem() != ItemStack.EMPTY && wolf.getOwner() != null && !handler.getTugOfWarStatus() && wolf.getTarget() == null){
                ItemStack wolfHeldItem = wolf.getMainHandItem();
                if(wolfHeldItem.getItem() instanceof MobPlushy || (wolfHeldItem.getItem() instanceof SwordItem && Thread.currentThread().getName() != "Server thread")){                    
                    //Nothing happens as this code is left to an entity goal
                } else if(handler.getWolfType() == WolfType.Fighter.getValue() && handler.getLevel(WolfStatsEnum.Intelligence) > 4){                
                    LogManager.getLogger().info(wolfHeldItem);
                    wolf.getOwner().spawnAtLocation(wolfHeldItem);                    
                    wolf.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
                else{                                        
                    int wolfSlot = wolfInventory.getAvailableSlot(wolfHeldItem);
                    if(wolfSlot >= 0){
                        ItemStack remaining = wolfInventory.insertItem(wolfSlot, wolfHeldItem, false);
                        wolf.setItemInHand(InteractionHand.MAIN_HAND, remaining);
                    }
                    else{
                        wolf.spawnAtLocation(wolfHeldItem);                    
                        wolf.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
            }
            List<Goal> goalsToAdd = handler.getUnaddedGoals();
            if(goalsToAdd.size() > 0){
                for (Goal nextGoal : goalsToAdd) {
                    WrappedGoal fullGoal = (WrappedGoal)nextGoal;
                    if(fullGoal.getGoal() instanceof TargetGoal){
                        wolf.targetSelector.addGoal(fullGoal.getPriority(), fullGoal.getGoal());
                    }
                    else{
                        wolf.goalSelector.addGoal(fullGoal.getPriority(), fullGoal.getGoal());
                    }
                }
                handler.clearUnaddedGoals();
            }
        }
    }
    @SubscribeEvent
    public void AddWolfGoals(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Wolf){
            Wolf wolf = (Wolf)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            
            FollowOwnerVariableGoal followOwnerVariableGoal = new FollowOwnerVariableGoal(wolf, 1.0D, 10.0F, 2.0F, false);           
            wolf.goalSelector.addGoal(6, followOwnerVariableGoal);
            if(handler.getWolfType() == 3)
                wolf.goalSelector.addGoal(8, new WolfBiasRoamGoal(wolf, 1.0, 10, 5));
            wolf.goalSelector.addGoal(2, new TugOfWarGaol(wolf));

            handler.getWolfPersonality().setWolfExpressions(wolf);
            
            handler.handleWolfGoals();          
        }
    }
    @SubscribeEvent
    public void OnLivingDeath(LivingDeathEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            EntityFinder<Wolf> entityFinder = new EntityFinder<Wolf>(player,Wolf.class);
            List<Wolf> wolves = entityFinder.findWithPredicate(10, 10,wolf -> wolf.getOwner() == player);
            //Why?
            List<ItemStack> playerInventory = Stream.concat(Stream.concat(player.getInventory().armor.stream(), player.getInventory().items.stream()),player.getInventory().offhand.stream()).collect(Collectors.toList());
            for (Wolf wolf : wolves) {
                IWolfStats handler = WolfStatsHandler.getHandler(wolf);
                if(handler.getRetrievalFlag()){
                    WolfItemStackHandler itemHandler = handler.getInventory();
                    int slotsAvailable = itemHandler.getNumberOfEmptySlots();
                    for (int i = 0; i < slotsAvailable; i++) {
                        if(wolf.getRandom().nextInt(50) < 100){
                            ItemStack nextItemToRetrieve = ItemStack.EMPTY;
                            while(nextItemToRetrieve == ItemStack.EMPTY && playerInventory.size() > 0) {
                                nextItemToRetrieve = playerInventory.get(wolf.getRandom().nextInt(playerInventory.size()));
                                playerInventory.remove(nextItemToRetrieve);
                            }
                            if(nextItemToRetrieve != ItemStack.EMPTY){
                                int slot = player.getInventory().findSlotMatchingItem(nextItemToRetrieve);
                                if(slot < 0){
                                    LogManager.getLogger().debug("slot is less than one...");
                                }
                                if(slot >= 0){
                                    itemHandler.insertIntoEmptySlot(nextItemToRetrieve.copy());                                
                                    player.getInventory().setItem(slot, ItemStack.EMPTY);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void SetLoot(LootingLevelEvent event){
        if(event.getDamageSource() != null && (event.getDamageSource().getDirectEntity() instanceof ServerPlayer || event.getDamageSource().getDirectEntity() instanceof Wolf)){
            LivingEntity user = (LivingEntity)event.getDamageSource().getDirectEntity();
            EntityFinder<Wolf> entityFinder = new EntityFinder<Wolf>(user,Wolf.class);
            List<Wolf> wolves = entityFinder.findWithPredicate(10, 10,wolf -> wolf.getOwner() == user);
            for (Wolf wolf : wolves) {
                IWolfStats handler = WolfStatsHandler.getHandler(wolf);
                if(handler.getLootFlag()){
                    event.setLootingLevel(event.getLootingLevel() + 1);
                }
            }
        }
    }

    public static Goal getWolfGoal(Wolf wolf, Class<?> goalType){
        Optional<WrappedGoal> optGoal = wolf.goalSelector.getRunningGoals().filter((goal) -> {
            return goal.getGoal().getClass() == goalType;
        }).findFirst();
        if(optGoal.isPresent()){
            return optGoal.get().getGoal();
        }
        return null;
    }
    public static void removeWolfGoal(Wolf wolf, Class<?> goalType){
        Optional<WrappedGoal> optGoal = wolf.goalSelector.getRunningGoals().filter((goal) -> {
            return goal.getGoal().getClass() == goalType;
        }).findFirst();
        if(optGoal.isPresent()){
            wolf.goalSelector.removeGoal(optGoal.get().getGoal());
        }
    }
}
