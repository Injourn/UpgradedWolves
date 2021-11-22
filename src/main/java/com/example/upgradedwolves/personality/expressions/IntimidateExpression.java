package com.example.upgradedwolves.personality.expressions;

import com.example.upgradedwolves.personality.Behavior;
import com.example.upgradedwolves.personality.CommonActionsController;
import com.example.upgradedwolves.utils.RandomRangeTimer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.DamageSource;

public class IntimidateExpression extends Expressions {
    private CommonActionsController controller;
    private RandomRangeTimer growl;
    private RandomRangeTimer jump;
    private RandomRangeTimer attack;
    private boolean canAttack = true;

    public IntimidateExpression(WolfEntity wolf, Behavior subBehavior) {
        super(wolf, subBehavior);
        maxEngagement = 250;
        controller = new CommonActionsController(wolf);
        growl = new RandomRangeTimer(40,40,wolf.getRNG());
        jump = new RandomRangeTimer(20,10,wolf.getRNG());
        growl.setFunction(() -> controller.growl());
        jump.setFunction(() -> controller.jumpLateral());
        attack = new RandomRangeTimer(35,5,wolf.getRNG());
        attack.setFunction(() -> {canAttack = true;});
    }

    @Override
    public void tick(){
        super.tick();
        wolf.getLookController().setLookPositionWithEntity(partner,0,0);    
        switch(subBehavior){
            case Playful:
                if(partner.getDistance(wolf) > 5){
                    jump.tick();
                } else {
                    wolf.getMoveHelper().strafe(-.25f, 0);
                }
                growl.tick();
                break;
            case Affectionate:
            case Social:
                if(partner.getDistance(wolf) < 5){
                    wolf.getMoveHelper().strafe(-.25f, 0);
                }
                growl.tick();
                break;
            case Shy:
                if(partner.getDistance(wolf) < 20){
                    wolf.getMoveHelper().strafe(-.25f, 0);
                }
                break;
            case Aggressive:
                if(partner.getDistance(wolf) < 2 && wolf.isOnGround()){
                    if(canAttack){
                        controller.jumpTowards(partner);
                        partner.attackEntityFrom(DamageSource.causeMobDamage(wolf),2);
                        canAttack = false;
                    }
                }
                if(!canAttack){
                    attack.tick();
                }
            case Dominant:
                if(partner.getDistance(wolf) > 5){
                    wolf.getMoveHelper().strafe(.25f,0);
                }
            case Lazy:
                growl.tick();
                break;
        }
    }

    @Override
    protected void changeState(int stateNumber) {
        arbitraryState = stateNumber;
    }

    @Override
    protected void changeEngagement() {
        float distance = partner.getDistance(wolf);
        if(distance >= 10)
            engagement -= (.03 * (distance-4.3) * (distance-4.3));
        else if(engagement < maxEngagement)
            engagement++;        
    }

    @Override
    protected void setDefaultEngagement() {
        engagement = 60;
    }

    @Override
    protected LivingEntity searchForPartner() {        
        return getNonFriendlyPartner();
    }
}
