package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.entities.goals.DigForItemGoal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;

public class DigForItemPowerUp extends PowerUp{

    public DigForItemPowerUp(int levelRequirement) {
        super(levelRequirement, "dig_for_item",DigForItemGoal.class);
        this.active = true;
        this.uLocation = 182;
        this.vLocation = 198;
        this.statType = WolfStatsEnum.values()[0];
        this.defaultPriority = 3;
    }

    @Override
    protected Goal goalConstructor(Wolf wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        return genericGoalConstructor(wolf);
    }
    
}
