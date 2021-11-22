package com.example.upgradedwolves.personality.expressions;

import com.example.upgradedwolves.personality.Behavior;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;

public class PassiveExpression extends Expressions {

    Vector3d position;
    int barkSpam;

    public PassiveExpression(WolfEntity wolf, Behavior subBehavior) {
        super(wolf, subBehavior);
        maxEngagement = 600;   
    }
    

    @Override
    public void resetTask(){
        this.wolf.getNavigator().clearPath();
        position = null;
    }

    @Override
    public void tick(){
        super.tick();
        wolf.getLookController().setLookPositionWithEntity(partner,0,0);
        switch(subBehavior){
            case Affectionate:
            case Social:
            case Playful:
            case Shy:
                runAway();
                break;
            case Aggressive:
                bark();
                runAway();
                break;
            case Dominant:
            case Lazy:
                //Do nothing..
                break;
        }
    }

    @Override
    protected void changeState(int stateNumber) {
        
        
    }

    @Override
    protected void changeEngagement() {
        if(partner.getDistance(wolf) >= 10)
            engagement--;
        else if(engagement < maxEngagement)
            engagement++;
    }

    @Override
    protected void setDefaultEngagement() {
        engagement = 60;
    }

    @Override
    protected LivingEntity searchForPartner() {
        //All monsters except skeletons
        return getNonFriendlyPartner();
    }

    private void runAway(){
        if(position == null || position.distanceTo(partner.getPositionVec()) < 10 || !movingToPosition()){
            position = RandomPositionGenerator.findRandomTargetBlockAwayFrom(wolf,20,5,partner.getPositionVec());
            if(position != null){
                Path path = wolf.getNavigator().getPathToPos(position.x,position. y, position.z, 0);
                wolf.getNavigator().setPath(path, 1);
            }
        }
    }

    private boolean movingToPosition(){
        return wolf.getNavigator().hasPath();
    }

    private void bark(){
        if(barkSpam-- <= 0){
            barkSpam = wolf.getRNG().nextInt(40) + 10;
            wolf.playAmbientSound();
        }
    }
}
