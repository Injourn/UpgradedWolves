package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.entities.goals.ThrowPotionGoal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;

public class ThrowPotionPowerUp extends PowerUp {

    public ThrowPotionPowerUp(int levelRequirement) {
        super(levelRequirement, "use_potion",ThrowPotionGoal.class);
        this.active = false;
        this.uLocation = 214;
        this.vLocation = 198;
        this.statType = WolfStatsEnum.values()[2];
        this.defaultPriority = 3;
    }

    @Override
    protected Goal goalConstructor(Wolf wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        return genericGoalConstructor(wolf);
    }
    
}
