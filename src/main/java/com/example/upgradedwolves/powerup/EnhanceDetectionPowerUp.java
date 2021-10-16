package com.example.upgradedwolves.powerup;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;

import net.minecraft.entity.passive.WolfEntity;

public class EnhanceDetectionPowerUp extends BonusStatPowerUp {

    public EnhanceDetectionPowerUp(int levelRequirement, int effectiveLevel, Double bonus) {
        super(levelRequirement, "enhance_detect", effectiveLevel);
        this.bonus = bonus;
        this.active = false;
        this.uLocation = 85;
        this.vLocation = 198;
        this.statType = WolfStatsEnum.values()[1];
    }

    @Override
    protected void enhanceAttribute(WolfEntity wolf, IWolfStats handler) {
        handler.addDetectionBonus(bonus);
    }   
    
}
