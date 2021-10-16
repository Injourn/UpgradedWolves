package com.example.upgradedwolves.powerup;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsEnum;

import net.minecraft.entity.passive.WolfEntity;

public class EnhanceAttackPowerUp extends BonusStatPowerUp {

    public EnhanceAttackPowerUp(int levelRequirement, int effectiveLevel, double bonus) {
        super(levelRequirement, "enhance_attack", effectiveLevel);
        this.bonus = bonus;
        this.active = false;
        this.uLocation = 86;
        this.vLocation = 182;
        this.statType = WolfStatsEnum.values()[1];        
    }

    @Override
    protected void enhanceAttribute(WolfEntity wolf, IWolfStats handler) {
        handler.addAttackBonus(bonus);
    }    
    
}
