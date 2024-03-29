package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.entities.goals.UseSwordGoal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.network.chat.Component;

public class UseSwordPowerUp extends PowerUp {

    public UseSwordPowerUp(int levelRequirement) {
        super(levelRequirement, "use_sword",UseSwordGoal.class);    
        this.active = false;
        this.uLocation = 134;
        this.vLocation = 182;
        this.statType = WolfStatsEnum.values()[1];
        this.defaultPriority = 4;    
    }

    @Override
    protected Goal goalConstructor(Wolf wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {        
        return genericGoalConstructor(wolf);
    }
    
    @Override
    public Component getDescription(Wolf wolf) {
        String name1 = wolf.hasCustomName() ? wolf.getCustomName().getString() : "Wolf";
        String name2 = wolf.hasCustomName() ? wolf.getCustomName().getString() + " has" : "they have";
        return Component.translatable(description,name1,name2);
    }
}
