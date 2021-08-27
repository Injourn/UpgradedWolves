package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.entities.goals.BarkStunGoal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;

public class BarkStunPowerUp extends PowerUp {

    public BarkStunPowerUp(int levelRequirement) {
        super(levelRequirement, UpgradedWolves.getId("powerups/bark_stun.json"), BarkStunGoal.class);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected Goal goalConstructor(WolfEntity wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {        
        return (Goal)relevantGoal.getDeclaredConstructors()[0].newInstance(wolf);
    }
    
}