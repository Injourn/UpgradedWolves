package com.example.upgradedwolves.entities.goals;

import com.example.upgradedwolves.capabilities.IWolfStats;

import org.apache.logging.log4j.LogManager;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class DetectEnemiesGoal extends Goal implements IUpdateableGoal {

    int currentTicks = 0;
    int attemptTicks = 300;
    int coolDownTicks = 3600;
    int lookAt = 100;
    double range;
    boolean coolDown = false;
    WolfEntity wolf;
    MonsterEntity detectedEntity;

    public DetectEnemiesGoal(WolfEntity wolf, double range){
        this.wolf = wolf;
        this.range = range;
    }

    @Override
    public boolean shouldExecute() {
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
        for(MonsterEntity monsterEntity : wolf.world.getEntitiesWithinAABB(MonsterEntity.class, wolf.getBoundingBox().grow(range, 0.0D, range))) {
            if (wolf.getEntitySenses().canSee(monsterEntity)) {                
                monsterEntity.addPotionEffect(new EffectInstance(Effect.get(24),600));
                this.wolf.getLookController().setLookPosition(monsterEntity.getPositionVec());
                detectedEntity = monsterEntity;
                currentTicks = 0;
                coolDown = true;
                LogManager.getLogger().debug("I have found an enemy. Going on coolDown");
                return true;
            }
        }            
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {        
        if(currentTicks++ < lookAt)
            return true;
        currentTicks = 0;
        return false;
    }
    

    @Override
    public void tick(){
        this.wolf.getLookController().setLookPosition(detectedEntity.getEyePosition(1.0F));
    }

    @Override
    public void Update(IWolfStats handler, WolfEntity wolf) {
        
    }
}
