package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.entities.goals.WolfTossArrowGoal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;

public class WolfTossArrowPowerUp extends PowerUp {

    public WolfTossArrowPowerUp(int levelRequirement) {
        super(levelRequirement, "toss_arrow",WolfTossArrowGoal.class);    
        this.active = true;
        this.uLocation = 230;
        this.vLocation = 182;
        this.statType = WolfStatsEnum.values()[1];
        this.defaultPriority = 4;    
    }

    @Override
    protected Goal goalConstructor(Wolf wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        
        return genericGoalConstructor(wolf);
    }
    
}
