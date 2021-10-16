package com.example.upgradedwolves.powerup;

import java.lang.reflect.InvocationTargetException;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.entities.goals.UseSwordGoal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
    protected Goal goalConstructor(WolfEntity wolf) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {        
        return genericGoalConstructor(wolf);
    }
    
    @Override
    public ITextComponent getDescription(WolfEntity wolf) {
        String name1 = wolf.hasCustomName() ? wolf.getCustomName().getString() : "Wolf";
        String name2 = wolf.hasCustomName() ? wolf.getCustomName().getString() + " has" : "they have";
        return new TranslationTextComponent(description,name1,name2);
    }
}
