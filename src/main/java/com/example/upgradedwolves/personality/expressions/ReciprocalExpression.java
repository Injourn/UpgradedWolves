package com.example.upgradedwolves.personality.expressions;

import com.example.upgradedwolves.personality.Behavior;
import com.example.upgradedwolves.personality.CommonActionsController;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;

public class ReciprocalExpression extends Expressions {
    CommonActionsController controller;


    public ReciprocalExpression(WolfEntity wolf, Behavior subBehavior) {
        super(wolf, subBehavior);
        controller = new CommonActionsController(wolf);
    }

    @Override
    public void tick(){
        if(arbitraryState == 1){
            chase();
        }else if(arbitraryState == 2){
            jumpAt();
        }else{
            glare();
        }
    }
    
    @Override
    protected void changeState(int stateNumber) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void changeEngagement() {
        engagement--;
    }

    @Override
    protected void setDefaultEngagement() {
        engagement = 300;
        
    }
    
    @Override
    protected LivingEntity searchForPartner() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setPartnerExternal(LivingEntity partner){
        if(!isActive()){
            this.partner = partner;
        }
    }
    
    private void glare() {
        wolf.getLookController().setLookPositionWithEntity(partner,0,0);
    }

    private void jumpAt() {
        controller.jumpTowards(partner);
    }

    private void chase() {
        wolf.getNavigator().tryMoveToEntityLiving(partner, 1);
    }
    
}
