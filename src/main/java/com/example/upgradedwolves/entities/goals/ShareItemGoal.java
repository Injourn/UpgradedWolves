package com.example.upgradedwolves.entities.goals;

import java.util.List;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.common.WolfPlayerInteraction;
import com.example.upgradedwolves.entities.utilities.EntityFinder;
import com.example.upgradedwolves.itemHandler.WolfItemStackHandler;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;

public class ShareItemGoal extends Goal {
    protected final Wolf wolf;
    protected final IWolfStats handler;
    protected Wolf target;
    protected EntityFinder<Wolf> allyFinder;
    protected int slot;

    public ShareItemGoal(Wolf wolf){
        this.wolf = wolf;
        this.handler = WolfStatsHandler.getHandler(wolf);
        allyFinder = new EntityFinder<Wolf>(this.wolf,Wolf.class);
    }

    @Override
    public boolean canUse() {
        WolfItemStackHandler wolfItems = handler.getInventory();
        List<Wolf> allyList = allyFinder.findWithPredicate(7, 3, ally -> (ally.getOwner() == wolf.getOwner()) &&
        (ally.getHealth() <= 10));
        for (Wolf livingEntity : allyList) {
            int slot = -1;
            if(livingEntity.getHealth() <= 10){
                slot = wolfItems.getArbitraryItemStack(item -> item.has(DataComponents.FOOD) && item.is(ItemTags.MEAT));
            }
            if(slot >= 0){
                this.slot = slot;
                target = livingEntity;
                break;
            }
        }
        return target != null;
    }

    @Override
    public void start() {
        ItemStack stack = handler.getInventory().extractItem(slot, 1, false);
        target.spawnAtLocation(stack);
        target = null;
    }

    //Needs to override continue using to make wolves ignore healing wolves.

    @Override
    public boolean canContinueToUse(){
        return false;
    }

    //TODO: find out if this needs to be deprecated
    private boolean wolfCriteria(Wolf wolf){
        WolfItemStackHandler wolfInventory = WolfStatsHandler.getHandler(wolf).getInventory();
        SelfPreservationGoal preservationGoal = (SelfPreservationGoal)WolfPlayerInteraction.getWolfGoal(wolf, SelfPreservationGoal.class);
        return wolfInventory.getArbitraryItemStack(item -> item.has(DataComponents.FOOD) && item.is(ItemTags.MEAT)) < 0 
        || preservationGoal == null || !preservationGoal.isEating;
    }
}
