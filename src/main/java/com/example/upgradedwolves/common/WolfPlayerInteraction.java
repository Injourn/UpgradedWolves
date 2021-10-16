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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class WolfPlayerInteraction {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityInteract(final EntityInteract event){
        //LogManager.getLogger().info(event.getEntity().toString());
        if(event.getTarget() instanceof WolfEntity){
            final WolfEntity wolf = (WolfEntity) event.getTarget();            
            final IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            if(handler.getTugOfWarStatus()){
                wolf.func_233687_w_(true);
                return;
            }
            if(wolf.getOwner() == event.getPlayer() && event.getPlayer().isCrouching()){
                if(Thread.currentThread().getName() == "Server thread"){
                    INamedContainerProvider wolfInventory = new ContainerProviderWolfInventory(wolf,handler.getInventory());
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putInt("strLevel", handler.getLevel(WolfStatsEnum.Strength));                    
                    nbt.putInt("spdLevel", handler.getLevel(WolfStatsEnum.Speed));
                    nbt.putInt("intLevel", handler.getLevel(WolfStatsEnum.Intelligence));
                    nbt.putFloat("strNum", handler.getStatRatio(WolfStatsEnum.Strength));
                    nbt.putFloat("spdNum", handler.getStatRatio(WolfStatsEnum.Speed));
                    nbt.putFloat("intNum", handler.getStatRatio(WolfStatsEnum.Intelligence));
                    nbt.putInt("wolfType", handler.getWolfType());
                    NetworkHooks.openGui((ServerPlayerEntity)event.getPlayer(),
                        wolfInventory,
                        (packetBuffer) ->{packetBuffer.writeInt(handler.getInventory().getSlots());packetBuffer.writeInt(wolf.getEntityId());packetBuffer.writeCompoundTag(nbt);}
                    );
                }
                wolf.func_233687_w_(!wolf.isSitting());
            }
            else{
                LogManager.getLogger().info(handler.getLevel(WolfStatsEnum.Love));
                LogManager.getLogger().info(handler.getWolfType());
                handler.InitLove();       
                final ItemStack foodItem = TrainingEventHandler.getFoodStack(event.getPlayer());
                final ItemStack goldenBoneItem = TrainingEventHandler.getPlayerHoldingItemStack(event.getPlayer(), GoldenBoneAbstract.class);
                final ItemStack tugOfWarRopeItem = TrainingEventHandler.getPlayerHoldingItemStack(event.getPlayer(), TugOfWarRopeItem.class);                
                if(foodItem != null){
                    final ITraining tHandler = TrainingHandler.getHandler(foodItem);
                    final int item = tHandler.getAttribute();                
                    if(item == 0)
                        return;
                    else{
                        handler.setWolfType(item);
                        handler.setWolffur(wolf.world.rand.nextInt(3));
                        handler.addGoals();
                        handler.handleWolfGoals();
                        foodItem.shrink(1);
                        tHandler.resetAttribute();
                        if(Thread.currentThread().getName() == "Server thread")
                            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> wolf), new RenderMessage( wolf.getEntityId(),WolfStatsHandler.getHandler(wolf).getWolfType(),handler.getWolfFur()));
                    }
                } else if (goldenBoneItem != null){
                    GoldenBoneAbstract goldenBone = (GoldenBoneAbstract)goldenBoneItem.getItem();
                    if(Thread.currentThread().getName() == "Server thread")
                        goldenBone.rightClickWolf(wolf,handler);
                    if(!event.getPlayer().isCreative())
                        goldenBoneItem.shrink(1);
                    wolf.func_233687_w_(!wolf.isSitting());
                } else if (tugOfWarRopeItem != null){
                    handler.setRopeHolder(event.getPlayer());
                    wolf.func_233687_w_(true);
                    tugOfWarRopeItem.shrink(1);
                }
                if(handler.getWolfType() ==  3){
                    if(handler.getRoamPoint() == null){
                        handler.setRoamPoint(wolf.getPosition());
                    } else {
                        handler.setRoamPoint(null);
                    }
                    wolf.func_233687_w_(true);
                }
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onEntitySpawn(LivingSpawnEvent event) {
        if(event.getEntity() instanceof WolfEntity){                
            WolfEntity wolf = (WolfEntity)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            wolf.setCanPickUpLoot(true);
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(handler.getWolfSpeed());
        }        
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onStartTracking(PlayerEvent.StartTracking event){        
        event.getTarget().getCapability(WolfStatsHandler.CAPABILITY_WOLF_STATS).ifPresent(capability -> {
            WolfEntity wolf = (WolfEntity)event.getTarget();
            PacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getPlayer()), new RenderMessage(wolf.getEntityId(),capability.getWolfType(),WolfStatsHandler.getHandler(wolf).getWolfFur()));
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(capability.getWolfSpeed());
        });
    }

    @SubscribeEvent
    public void onWolfJump(LivingJumpEvent event){
        if(event.getEntity() instanceof WolfEntity){
            WolfEntity wolf = (WolfEntity)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            //Scavenger Wolf Bonus
            handler.addXp(WolfStatsEnum.Speed,(handler.getWolfType() == 2 ? 1 : 0));
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> wolf), new RenderMessage( wolf.getEntityId(),handler.getWolfType(),handler.getWolfFur()) );
        }
    }
    @SubscribeEvent
    public void onWolfDestroyBlock(LivingDestroyBlockEvent event){
        if(event.getEntity() instanceof WolfEntity){
            WolfEntity wolf = (WolfEntity)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            handler.addXp(WolfStatsEnum.Strength,1);
            handler.addXp(WolfStatsEnum.Intelligence,(handler.getWolfType() == 3 ? 2 : 1));
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> wolf), new RenderMessage( wolf.getEntityId(),handler.getWolfType(),handler.getWolfFur()) );
        }
    }
    @SubscribeEvent
    public void onWolfPickUp(LivingUpdateEvent event){        
        if(event.getEntity() instanceof WolfEntity){
            WolfEntity wolf = (WolfEntity)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            WolfItemStackHandler wolfInventory = handler.getInventory();

            Optional<PrioritizedGoal> optGoal = wolf.goalSelector.getRunningGoals().filter((goal) -> {
                return goal.getGoal().getClass() == FollowOwnerGoal.class;
            }).findFirst();
            if(optGoal.isPresent()){
                wolf.goalSelector.removeGoal(optGoal.get().getGoal());
            }
            if(handler.getWolfType() == 3)
                removeWolfGoal(wolf, WaterAvoidingRandomWalkingGoal.class);

            wolf.setCanPickUpLoot(false);
            for(ItemEntity itementity : wolf.world.getEntitiesWithinAABB(ItemEntity.class, wolf.getBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
                if (wolfInventory.getAvailableSlot(itementity.getItem()) >= 0) {
                    wolf.setCanPickUpLoot(true);
                }
            }

            if(wolf.getHeldItemMainhand() != ItemStack.EMPTY && wolf.getOwner() != null && !handler.getTugOfWarStatus() && wolf.getAttackTarget() == null){
                ItemStack wolfHeldItem = wolf.getHeldItemMainhand();
                if(wolfHeldItem.getItem() instanceof MobPlushy || (wolfHeldItem.getItem() instanceof SwordItem && Thread.currentThread().getName() != "Server thread")){                    
                    //Nothing happens as this code is left to an entity goal
                } else if(handler.getWolfType() == WolfType.Fighter.getValue() && handler.getLevel(WolfStatsEnum.Intelligence) > 4){                
                    LogManager.getLogger().info(wolfHeldItem);
                    wolf.getOwner().entityDropItem(wolfHeldItem);                    
                    wolf.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                }
                else{                                        
                    int wolfSlot = wolfInventory.getAvailableSlot(wolfHeldItem);
                    if(wolfSlot >= 0){
                        ItemStack remaining = wolfInventory.insertItem(wolfSlot, wolfHeldItem, false);
                        wolf.setHeldItem(Hand.MAIN_HAND, remaining);
                    }
                    else{
                        wolf.entityDropItem(wolfHeldItem);                    
                        wolf.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
            }
            List<Goal> goalsToAdd = handler.getUnaddedGoals();
            if(goalsToAdd.size() > 0){
                for (Goal nextGoal : goalsToAdd) {
                    PrioritizedGoal fullGoal = (PrioritizedGoal)nextGoal;
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
    public void AddWolfGoals(EntityJoinWorldEvent event){
        if(event.getEntity() instanceof WolfEntity){
            WolfEntity wolf = (WolfEntity)event.getEntity();
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            
            FollowOwnerVariableGoal followOwnerVariableGoal = new FollowOwnerVariableGoal(wolf, 1.0D, 10.0F, 2.0F, false);           
            wolf.goalSelector.addGoal(6, followOwnerVariableGoal);            
            wolf.goalSelector.addGoal(8, new WolfBiasRoamGoal(wolf, 1.0, 10, 5));
            wolf.goalSelector.addGoal(2, new TugOfWarGaol(wolf));
            
            handler.handleWolfGoals();          
        }
    }
    @SubscribeEvent
    public void OnLivingDeath(LivingDeathEvent event){
        if(event.getEntity() instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity)event.getEntity();
            EntityFinder<WolfEntity> entityFinder = new EntityFinder<WolfEntity>(player,WolfEntity.class);
            List<WolfEntity> wolves = entityFinder.findWithPredicate(10, 10,wolf -> wolf.getOwner() == player);
            //Why?
            List<ItemStack> playerInventory = Stream.concat(Stream.concat(player.inventory.armorInventory.stream(), player.inventory.mainInventory.stream()),player.inventory.offHandInventory.stream()).collect(Collectors.toList());
            for (WolfEntity wolf : wolves) {
                IWolfStats handler = WolfStatsHandler.getHandler(wolf);
                if(handler.getRetrievalFlag()){
                    WolfItemStackHandler itemHandler = handler.getInventory();
                    int slotsAvailable = itemHandler.getNumberOfEmptySlots();
                    for (int i = 0; i < slotsAvailable; i++) {
                        if(wolf.getRNG().nextInt(50) < 100){
                            ItemStack nextItemToRetrieve = ItemStack.EMPTY;
                            while(nextItemToRetrieve == ItemStack.EMPTY && playerInventory.size() > 0) {
                                nextItemToRetrieve = playerInventory.get(wolf.getRNG().nextInt(playerInventory.size()));
                                playerInventory.remove(nextItemToRetrieve);
                            }
                            if(nextItemToRetrieve != ItemStack.EMPTY){
                                int slot = player.inventory.getSlotFor(nextItemToRetrieve);
                                if(slot < 0){
                                    LogManager.getLogger().debug("slot is less than one...");
                                }
                                if(slot >= 0){
                                    itemHandler.insertIntoEmptySlot(nextItemToRetrieve.copy());                                
                                    player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
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
        if(event.getDamageSource().getTrueSource() instanceof ServerPlayerEntity || event.getDamageSource().getTrueSource() instanceof WolfEntity){
            LivingEntity user = (LivingEntity)event.getDamageSource().getTrueSource();
            EntityFinder<WolfEntity> entityFinder = new EntityFinder<WolfEntity>(user,WolfEntity.class);
            List<WolfEntity> wolves = entityFinder.findWithPredicate(10, 10,wolf -> wolf.getOwner() == user);
            for (WolfEntity wolf : wolves) {
                IWolfStats handler = WolfStatsHandler.getHandler(wolf);
                if(handler.getLootFlag()){
                    event.setLootingLevel(event.getLootingLevel() + 1);
                }
            }
        }
    }

    public static Goal getWolfGoal(WolfEntity wolf, Class<?> goalType){
        Optional<PrioritizedGoal> optGoal = wolf.goalSelector.getRunningGoals().filter((goal) -> {
            return goal.getGoal().getClass() == goalType;
        }).findFirst();
        if(optGoal.isPresent()){
            return optGoal.get().getGoal();
        }
        return null;
    }
    public static void removeWolfGoal(WolfEntity wolf, Class<?> goalType){
        Optional<PrioritizedGoal> optGoal = wolf.goalSelector.getRunningGoals().filter((goal) -> {
            return goal.getGoal().getClass() == goalType;
        }).findFirst();
        if(optGoal.isPresent()){
            wolf.goalSelector.removeGoal(optGoal.get().getGoal());
        }
    }
}