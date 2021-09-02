package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.entities.goals.WolfFleeExplodingCreeper;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;

public class FleeCreeperPowerUp extends PowerUp {

    public FleeCreeperPowerUp(int levelRequirement) {
        super(levelRequirement, "flee_creeper",WolfFleeExplodingCreeper.class);        
    }

    @Override
    protected Goal goalConstructor(WolfEntity wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {        
        return (Goal)relevantGoal.getDeclaredConstructors()[0].newInstance(wolf, 7.0F, 1.5D, 1.5D);
    }
    
}
