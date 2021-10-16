package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.entities.goals.SelfPreservationGoal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;

public class SelfPreservationPowerUp extends PowerUp {

    public SelfPreservationPowerUp(int levelRequirement) {
        super(levelRequirement, "self_preservation",SelfPreservationGoal.class);   
        this.active = false;
        this.uLocation = 150;
        this.vLocation = 198;
        this.statType = WolfStatsEnum.values()[0];
        this.defaultPriority = 3;     
    }

    @Override
    protected Goal goalConstructor(WolfEntity wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        return genericGoalConstructor(wolf);
    }
    
}
