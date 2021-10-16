package com.example.upgradedwolves.powerup;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;

import net.minecraft.entity.passive.WolfEntity;

public class EnhanceSpeedPowerUp extends BonusStatPowerUp {


    public EnhanceSpeedPowerUp(int levelRequirement, int effectiveLevel,Double bonus) {
        super(levelRequirement, "enhance_speed", effectiveLevel);
        this.bonus = bonus;
        this.active = false;
        this.uLocation = 102;
        this.vLocation = 198;
        this.statType = WolfStatsEnum.values()[1];        
    }

    @Override
    protected void enhanceAttribute(WolfEntity wolf, IWolfStats handler) {
        handler.addSpeedBonus(bonus);
    }   
    
}
