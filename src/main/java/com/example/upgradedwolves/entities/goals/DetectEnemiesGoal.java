package com.example.upgradedwolves.entities.goals;

import java.util.List;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.entities.utilities.AbilityEnhancer;
import com.example.upgradedwolves.entities.utilities.EntityFinder;

import org.apache.logging.log4j.LogManager;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Wolf;

public class DetectEnemiesGoal extends Goal implements IUpdateableGoal {

    int currentTicks = 0;
    int attemptTicks = 300;
    int coolDownTicks = 3600;
    int lookAt = 100;
    double range;
    boolean coolDown = false;
    Wolf wolf;
    Monster detectedEntity;
    EntityFinder<Monster> entityFinder;

    public DetectEnemiesGoal(Wolf wolf){
        this.wolf = wolf;
        IWolfStats handler = WolfStatsHandler.getHandler(wolf);
        this.range = 5 + handler.getDetectionBonus();
        entityFinder = new EntityFinder<Monster>(wolf,Monster.class);
    }

    @Override
    public boolean canUse() {
        if(coolDown)
            if(currentTicks++ < coolDownTicks)
                return false;
            else{
                coolDown = false;
                currentTicks = 0;
                LogManager.getLogger().debug("Cool down has ended");

            }
        if(currentTicks++ < attemptTicks)
            return false;
        List<Monster> foundEntities = entityFinder.findWithPredicate(range, 0, x -> wolf.getSensing().hasLineOfSight(x));
        foundEntities.addAll(entityFinder.findWithinRange(range/5, 0));
        for(Monster monsterEntity : foundEntities) {
            monsterEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING,120 + (10 * AbilityEnhancer.detectionSkill(wolf))));
            this.wolf.getLookControl().setLookAt(monsterEntity.getPosition(1));
            detectedEntity = monsterEntity;
            currentTicks = 0;
            coolDown = true;
            LogManager.getLogger().debug("I have found an enemy. Going on coolDown");
            return true;            
        }            
        return false;
    }

    @Override
    public boolean canContinueToUse() {        
        if(currentTicks++ < lookAt)
            return true;
        currentTicks = 0;
        return false;
    }
    

    @Override
    public void tick(){
        this.wolf.getLookControl().setLookAt(detectedEntity.getEyePosition(1.0F));
    }

    @Override
    public void Update(IWolfStats handler, Wolf wolf) {
        range = 5 + handler.getDetectionBonus();
    }
}
