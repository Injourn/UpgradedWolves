package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.entities.goals.WolfFindAndPickUpItemGoal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;

public class PickUpItemPowerUp extends PowerUp {

    public PickUpItemPowerUp(int levelRequirement) {
        super(levelRequirement, "pick_up_item",WolfFindAndPickUpItemGoal.class);   
        this.active = false;
        this.uLocation = 198;
        this.vLocation = 182;
        this.statType = WolfStatsEnum.values()[2];
        this.defaultPriority = 3;     
    }

    @Override
    protected Goal goalConstructor(Wolf wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {        
        return (Goal)relevantGoal.getDeclaredConstructors()[0].newInstance(wolf);
    }
    
}
