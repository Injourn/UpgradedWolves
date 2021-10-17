package com.example.upgradedwolves.entities.goals;

import java.util.List;
import java.util.Random;

import com.example.upgradedwolves.entities.utilities.AbilityEnhancer;
import com.example.upgradedwolves.entities.utilities.EntityFinder;
import com.example.upgradedwolves.network.PacketHandler;
import com.example.upgradedwolves.network.message.CreateParticleForMobMessage;

import net.minecraft.entity.IAngerable;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.PacketDistributor;

public class BarkStunGoal extends CoolDownGoal {
    protected final EntityFinder<MonsterEntity> entityFinder;
    protected final WolfEntity wof;
    private List<MonsterEntity> enemies;
    private final Random rand;
    private int stunDuration = 30;

    public BarkStunGoal(WolfEntity wolf){
        this.wof = wolf;
        this.entityFinder = new EntityFinder<>(wof, MonsterEntity.class);
        stunDuration = 30 + AbilityEnhancer.minMaxIncrease(wof, 90, 10, 50);
        setCoolDownInSeconds(20);
        rand = new Random();
    }

    @Override
    public boolean shouldExecute() {
        
        if(active()){
            List<MonsterEntity> entityList = entityFinder.findWithPredicate(5, 2, enemy -> !(enemy instanceof IAngerable) || ((IAngerable)enemy).getAttackTarget() != null);
            if(entityList.size() > 0){
                enemies = entityList;
                return true;
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        wof.playSound(SoundEvents.ENTITY_WOLF_AMBIENT, 20, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.02F + .7F);        
        for (MonsterEntity mobEntity : enemies) {    
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> wof),new CreateParticleForMobMessage(mobEntity.getEntityId(), ParticleTypes.FLAME, 10));
            mobEntity.setNoAI(true);            
        }
        startCoolDown();
    }

    @Override
    public boolean shouldContinueExecuting() {
        int bonus = AbilityEnhancer.minMaxIncrease(wof, 90, 10, 50);
        if(stunDuration-- <= 0){
            for (MonsterEntity mobEntity : enemies) {
                mobEntity.setNoAI(false);
            }
            stunDuration = 50 + bonus;
            return false;
        }
        return true;
    }
}
