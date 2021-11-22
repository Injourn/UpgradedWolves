package com.example.upgradedwolves.entities.goals;

import java.util.EnumSet;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;

public class WolfFleeExplodingCreeper extends FleeExplodingCreeper implements IUpdateableGoal {
    WolfEntity wolf;

    public WolfFleeExplodingCreeper(WolfEntity entityIn, float avoidDistanceIn, double farSpeedIn,
            double nearSpeedIn) {
        super(entityIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
        this.wolf = entityIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE,Goal.Flag.LOOK));
    }
    
    @Override
    public boolean shouldExecute(){
        if(WolfStatsHandler.getHandler(wolf).getLevel(WolfStatsEnum.Intelligence) > 5)
            return super.shouldExecute();
        return false;
    }

    @Override
    public void Update(IWolfStats handler, WolfEntity wolf) {
    }
}
