package com.example.upgradedwolves.personality.expressions;

import com.example.upgradedwolves.personality.Behavior;
import com.example.upgradedwolves.utils.RandomRangeTimer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;

public class SocializeExpression extends Expressions {
    protected RandomRangeTimer observe;
    protected boolean available;
    protected Vector3d position;

    public SocializeExpression(WolfEntity wolf, Behavior subBehavior) {
        super(wolf, subBehavior);
        maxEngagement = 140;
        observe = new RandomRangeTimer(140,140,wolf.getRNG());
        observe.setFunction(() -> {available = true;});
    }

    @Override
    public void tick(){
        super.tick();
        wolf.getLookController().setLookPositionWithEntity(partner,0,0);
        switch(subBehavior){
            case Affectionate:
            case Social:
            case Playful:                
            case Aggressive:                
            case Dominant:
                runTowards();
                break;
            case Shy:
            case Lazy:                
                break;
        }
    }

    @Override
    protected void changeState(int stateNumber) {
        
        
    }

    @Override
    protected void changeEngagement() {
        engagement--;
    }

    @Override
    protected void setDefaultEngagement() {
        engagement = 80 + wolf.getRNG().nextInt(60);
        
    }

    @Override
    protected LivingEntity searchForPartner() {
        if(available){
            available = false;
            return getNonFriendlyPartner();
        }
        observe.tick();
        return null;
    }
    
    private void runTowards(){
        if(position == null || wolf.getPositionVec().distanceTo(partner.getPositionVec()) > 5 || !wolf.getNavigator().hasPath()){
            position = RandomPositionGenerator.findRandomTargetTowardsScaled(wolf,20,5,partner.getPositionVec(),1);
            if(position != null){
                Path path = wolf.getNavigator().getPathToPos(position.x,position. y, position.z, 0);
                wolf.getNavigator().setPath(path, 1);
            }
        }
    }
}
