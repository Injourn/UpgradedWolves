package com.example.upgradedwolves.entities.goals;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.itemHandler.WolfItemStackHandler;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;

public class SelfPreservationGoal extends Goal {
    public Wolf wolf;
    final int eatingTime = 20 * 3;
    int currentTime = 0;
    int healAmount;
    boolean isEating;

    public SelfPreservationGoal(Wolf wolf){
        this.wolf = wolf;
        isEating = false;
    }

    @Override
    public boolean canUse() {
        IWolfStats handler = WolfStatsHandler.getHandler(wolf);
        WolfItemStackHandler wolfInventory = handler.getInventory();
        if(wolf.getHealth() < 15 && wolfInventory.getArbitraryItemStack(item -> item.has(DataComponents.FOOD)) >= 0){
            int inventorySlot = wolfInventory.getArbitraryItemStack(item -> item.has(DataComponents.FOOD));
            ItemStack foodStack = wolfInventory.getStackInSlot(inventorySlot);
            healAmount = foodStack.getComponents().get(DataComponents.FOOD).nutrition();
            currentTime = 0;
            foodStack.shrink(1);
            isEating = true;
            return true;
        }
        healAmount = 0;
        return false;
    }

    public boolean canContinueToUse(){
        if(currentTime++ < eatingTime){
            if(currentTime % 4 == 0)
            wolf.playSound(SoundEvents.GENERIC_EAT, 0.5F + 0.5F * (float)wolf.getRandom().nextInt(2), (wolf.getRandom().nextFloat() - wolf.getRandom().nextFloat()) * 0.2F + 1.0F);
            return true;
        }   
        wolf.heal(healAmount);
        healAmount = 0;
        isEating = false;
        return false;
    }

    public boolean isEating(){
        return isEating;
    }
    
}
