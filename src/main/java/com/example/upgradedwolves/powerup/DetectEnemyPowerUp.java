package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.entities.goals.DetectEnemiesGoal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;

public class DetectEnemyPowerUp extends PowerUp {

    public DetectEnemyPowerUp(int levelRequirement) {
        super(levelRequirement, UpgradedWolves.getId("powerups/detect_enemy.json"),DetectEnemiesGoal.class);
    }

    @Override
    protected Goal goalConstructor(WolfEntity wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {        
        return (Goal)relevantGoal.getDeclaredConstructors()[0].newInstance(wolf);
    }
    
}
