package com.example.upgradedwolves.entities.goals;

import java.util.EnumSet;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.entities.WolfChaseableEntity;
import com.example.upgradedwolves.entities.plushy.MobPlushyEntity;
import com.example.upgradedwolves.itemHandler.WolfItemStackHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class WolfFindAndPickUpItemGoal extends Goal implements IUpdateableGoal{    
    WolfEntity wolf;
    WolfItemStackHandler wolfInventory;
    Entity item;
    int unseenMemoryTicks;
    int targetUnseenTicks;
    double distance;
    Vector3d initialPoint;
    Vector3d endPoint;


    public WolfFindAndPickUpItemGoal(WolfEntity owner){
        this.wolf = owner;
        IWolfStats handler = WolfStatsHandler.getHandler(wolf);
        this.wolfInventory = handler.getInventory();
        this.unseenMemoryTicks = 10 * handler.getLevel(WolfStatsEnum.Intelligence);
        distance = 12.0D + handler.getDetectionBonus();
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE,Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        item = null;
        if(item != null || wolf.isSitting() || wolf.getHeldItemMainhand() != ItemStack.EMPTY)
            return false;
        for(ItemEntity itementity : wolf.world.getEntitiesWithinAABB(ItemEntity.class, wolf.getBoundingBox().grow(12.0D, 0.0D, 12.0D))) {
            if (wolfInventory.getAvailableSlot(itementity.getItem()) >= 0 && canEasilyReach(itementity)) {                
                item = itementity;
                return true;
            }
        }
        for(WolfChaseableEntity wolfToy : wolf.world.getEntitiesWithinAABB(WolfChaseableEntity.class, wolf.getBoundingBox().grow(36.0D, 5.0D, 36.0D))){
            if (wolfInventory.getAvailableSlot(wolfToy.getItem()) >= 0 && canEasilyReach(wolfToy)){
                item = wolfToy;
                initialPoint = wolf.getPositionVec();
                return true;
            }
        }
        for(MobPlushyEntity mobPlushy : wolf.world.getEntitiesWithinAABB(MobPlushyEntity.class, wolf.getBoundingBox().grow(12.0D, 0.0D, 12.0D))){
            if (wolfInventory.getAvailableSlot(mobPlushy.getItem()) >= 0 && canEasilyReach(mobPlushy)){
                item = mobPlushy;
                return true;
            }
        }
        return false;
    }

    public boolean shouldContinueExecuting() {
        if(item instanceof ItemEntity){
            ItemEntity item = (ItemEntity)wolf.world.getEntityByID(this.item.getEntityId());
            if (item == null || wolfInventory.getAvailableSlot(item.getItem()) < 0) {
                return false;
            } else {
                return shouldChase(1,item);
            }
        } else if(item instanceof WolfChaseableEntity) {
            WolfChaseableEntity item = (WolfChaseableEntity)wolf.world.getEntityByID(this.item.getEntityId());
            if (endPoint != null){
                double distance = initialPoint.distanceTo(endPoint);
                IWolfStats stats = WolfStatsHandler.getHandler(wolf);
                stats.addXp(WolfStatsEnum.Speed, (int)(distance/2));
                endPoint = null;
                initialPoint = null;
                return false;
            }
            else if (item == null || wolfInventory.getAvailableSlot(item.getItem()) < 0) {
                return false;
            } else {
                return shouldChase(3, item);
            }
        } else{
            MobPlushyEntity item = (MobPlushyEntity)wolf.world.getEntityByID(this.item.getEntityId());
            if (item == null || wolfInventory.getAvailableSlot(item.getItem()) < 0 || wolf.getHeldItemMainhand() != ItemStack.EMPTY) {
                return false;
            } else {
                return shouldChase(1,item);
            }
        }
    }

    private boolean shouldChase(double multiplier, Entity item){       
        double d0 = this.getTargetDistance();
        if (wolf.getDistanceSq(item) > d0 * d0 * multiplier) {
            this.item = null;
            return false;
        } else {
            if (wolf.getEntitySenses().canSee(item)) {
                this.targetUnseenTicks = 0;
            } else if (++this.targetUnseenTicks > this.unseenMemoryTicks * multiplier) {
                this.item = null;
                return false;
            }                
            return true; 
        }        
    }

    protected double getTargetDistance() {
        return wolf.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
    private boolean canEasilyReach(Entity target) {        
        Path path = wolf.getNavigator().getPathToEntity(target, 0);
        if (path == null) {
           return false;
        } else {
           PathPoint pathpoint = path.getFinalPathPoint();
           if (pathpoint == null) {
              return false;
           } else {
              int i = pathpoint.x - MathHelper.floor(target.getPosX());
              int j = pathpoint.z - MathHelper.floor(target.getPosZ());
              return (double)(i * i + j * j) <= 2.25D;
           }
        }
    }

    @Override
    public void tick(){
        wolf.getMoveHelper().setMoveTo(item.getPosX(), item.getPosY(), item.getPosZ(), 1.0);
    }

    @Override
    public void Update(IWolfStats handler, WolfEntity wolf) {        
        distance = 12.0D + handler.getDetectionBonus();
    }
    public void setEndPoint(Vector3d end){
        endPoint = end;
    }
}
