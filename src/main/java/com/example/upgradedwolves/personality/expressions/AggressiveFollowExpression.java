package com.example.upgradedwolves.personality.expressions;

import java.util.EnumSet;

import com.example.upgradedwolves.personality.Behavior;
import com.example.upgradedwolves.personality.CommonActionsController;
import com.example.upgradedwolves.utils.RandomRangeTimer;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;

public class AggressiveFollowExpression extends Expressions {
    protected RandomRangeTimer whine;
    protected CommonActionsController controller;


    public AggressiveFollowExpression(Wolf wolf, Behavior subBehavior) {
        super(wolf, subBehavior);
        maxEngagement = 100;
        controller =  new CommonActionsController(wolf);
        whine = new RandomRangeTimer(40,200,wolf.getRandom());
        whine.setFunction(() -> controller.whine());
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.TARGET));
    }

    @Override
    public void tick(){
        super.tick();
        subBehaviorTick();
    }

    @Override
    protected void subBehaviorTick() {
        switch(subBehavior){
            case Affectionate:
                attach(1.25f,1.4f);
                break;
            case Social:
            case Playful:
                attach(2f,1f);
                break;
            case Aggressive:
            case Dominant:
                attach(1.5f,1.3f);    
                break;
            case Shy:
            case Lazy:
                attach(2.5f,.75f);
                break;
            case EMPTY:
                break;
        }
    }

    @Override
    protected void changeState(int stateNumber) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void changeEngagement() {
        if(partner.distanceTo(wolf) > 3)
            engagement--;
        else if(engagement < maxEngagement)
            engagement++;
        
    }

    @Override
    protected void setDefaultEngagement() {        
        engagement = 50;
    }

    @Override
    protected LivingEntity searchForPartner() {
        whine.tick();     
        return getAnotherWolfOrOwner();
    }

    private void attach(float distance, float speed){
        float partnerDistance = partner.distanceTo(wolf);
        if(partnerDistance > distance){
            wolf.getNavigation().moveTo(partner,partnerDistance > distance * 2 ? 1.3 : 1);
        }
    }
    
}
