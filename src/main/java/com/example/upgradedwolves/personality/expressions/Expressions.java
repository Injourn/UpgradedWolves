package com.example.upgradedwolves.personality.expressions;

import java.util.EnumSet;
import java.util.List;

import com.example.upgradedwolves.entities.utilities.EntityFinder;
import com.example.upgradedwolves.personality.Behavior;

import org.apache.logging.log4j.LogManager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;

public abstract class Expressions extends Goal {
    //Add Wolf Behavior
    public final WolfEntity wolf;
    
    protected int arbitraryState;
    protected Behavior subBehavior;
    protected boolean waitingForInvite;
    protected int engagement;
    protected int maxEngagement;
    protected int defaultState = 0;
    
    LivingEntity partner;

    public Expressions(WolfEntity wolf,Behavior subBehavior){
        this.wolf = wolf;
        this.subBehavior = subBehavior;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK,Goal.Flag.TARGET));
    }

    @Override
    public boolean shouldExecute(){
        arbitraryState = defaultState;
        partner = searchForPartner();
        setDefaultEngagement();
        return partner != null;
    }

    @Override
    public void tick(){
        changeEngagement();
    }

    @Override
    public boolean shouldContinueExecuting(){
        if(bored()){
            partner = null;
            return false;
        }
        return true;
    }

    public void reciprocateAction(WolfEntity wolfEntity){

    }

    public boolean isActive(){
        return engagement > 0 && partner != null;
    }
    
    protected abstract void changeState(int stateNumber);
    
    protected abstract void changeEngagement();

    protected abstract void setDefaultEngagement();

    protected abstract LivingEntity searchForPartner();


    protected void attacked(LivingEntity attackedBy){

    }        

    protected boolean bored(){
        return engagement <= 0 || partner == null;
    }

    protected LivingEntity getRandomPartner(){
        EntityFinder<LivingEntity> finder = new EntityFinder<LivingEntity>(wolf,LivingEntity.class);
        return getRandomPartner(finder);
    }

    protected <T extends LivingEntity> LivingEntity getRandomPartner(EntityFinder<T> finder){
        List<T> entities = finder.findWithinRange(10.0, 5.0);
        return setPartnerFromList(entities);
    }

    protected LivingEntity getOwnerAsPartner(){
        if(wolf.isTamed()){
            return wolf.getOwner();
        } else {
            LogManager.getLogger().info("Wolf is not tame, cannot set owner as partner");
            return null;
        }
    }

    protected LivingEntity getAnotherWolfOrOwner(){
        EntityFinder<LivingEntity> finder = new EntityFinder<LivingEntity>(wolf,LivingEntity.class);
        List<LivingEntity> entities = finder.findWithPredicate(10, 5, x -> (x instanceof WolfEntity && sameSide((WolfEntity)x)) || x == wolf.getOwner());
        return setPartnerFromList(entities);
    }

    protected LivingEntity getNonFriendlyPartner(){
        EntityFinder<LivingEntity> playerOrMonster = new EntityFinder<LivingEntity>(wolf,LivingEntity.class);
        List<LivingEntity> entities = playerOrMonster.findWithPredicate(10, 5, x -> (x instanceof MonsterEntity && !(x instanceof AbstractSkeletonEntity)) || (x instanceof PlayerEntity && !isOwner((PlayerEntity)x)) || (x instanceof WolfEntity && !sameSide((WolfEntity)x) && x != wolf));
        return setPartnerFromList(entities);
    }
    
    
    protected <T> T setPartnerFromList(List<T> entities){
        if(entities.size() > 0){
            int rand = wolf.getRNG().nextInt(entities.size());
            return entities.get(rand);
        }
        return null;
    }

    private boolean isOwner(PlayerEntity player){
        return wolf.isTamed() && wolf.getOwner() == player;
    }

    private boolean shareOwner(WolfEntity otherWolf){
        return otherWolf.isTamed() && otherWolf.getOwner() == wolf.getOwner() && otherWolf != wolf;
    }

    private boolean sameSide(WolfEntity otherWolf){
        return (shareOwner(otherWolf) || (!wolf.isTamed() && !otherWolf.isTamed())) && otherWolf != wolf;
    }

}
