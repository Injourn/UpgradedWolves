package com.example.upgradedwolves.personality.expressions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.upgradedwolves.personality.Behavior;
import com.example.upgradedwolves.personality.CommonActionsController;
import com.example.upgradedwolves.utils.RandomRangeTimer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;

public class PlayfulExpression extends Expressions {
    protected CommonActionsController controller;
    protected Vector3d position;
    protected int x = 1;
    protected int z = 1;
    protected boolean flipX;
    protected RandomRangeTimer play;
    protected boolean available;
    protected int partnerState;


    public PlayfulExpression(WolfEntity wolf, Behavior subBehavior) {
        super(wolf, subBehavior);
        maxEngagement = 300;
        controller = new CommonActionsController(wolf);
        //play = new RandomRangeTimer(1200,4800,wolf.getRandom());
        play = new RandomRangeTimer(100,100,wolf.getRNG());
        play.setFunction(() -> {available = true;});
    }

    @Override
    public void startExecuting(){
        super.startExecuting();
        x = 1;
        z = 1;
    }

    @Override
    public void tick(){
        super.tick();
        if(partner instanceof WolfEntity){
            //get other wolf's behavior to determine play type.
        }
        switch(subBehavior){
            case Affectionate:
            case Social:
            case Playful:
                runAround();
                break;
            case Shy:
            case Aggressive:
                chase();
                break;
            case Dominant:
            case Lazy:
                annoy();
                break;
        }
        setPartnerPlayType();
    }
    @Override
    public void reciprocateAction(WolfEntity otherWolf){
        if(arbitraryState == 1){
            otherWolf.getMoveHelper().setMoveTo(wolf.getPosX(), wolf.getPosY(), wolf.getPosZ(), 1);
        } else {
            otherWolf.getLookController().setLookPositionWithEntity(wolf,0,0);
        }
    }

    @Override
    protected void changeState(int stateNumber) {
        partnerState = stateNumber;
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
        if(available){
            available = false;
            LivingEntity potentialPlaymate = getAnotherWolfOrOwner();
            if(potentialPlaymate instanceof WolfEntity){
                WolfEntity wolfPlaymate = (WolfEntity)potentialPlaymate;

                List<PrioritizedGoal> excludeGoals = wolfPlaymate.goalSelector.getRunningGoals().filter((goal) -> {
                    return goal.getGoal() instanceof Expressions && isPlaying((Expressions)goal.getGoal());
                }).collect(Collectors.toList());
                if(excludeGoals.size() > 0)
                    return null;

                wolfPlaymate.goalSelector.getRunningGoals().filter((goal) -> {
                    return goal.getGoal() instanceof Expressions;
                }).collect(Collectors.toList()).forEach(x -> x.resetTask());
                Optional<PrioritizedGoal> recipGoal = getWolfReciprocalExpression(wolfPlaymate);
                if(!recipGoal.isPresent()){
                    return null;
                }
                recipGoal.get().startExecuting();
                ReciprocalExpression expression = (ReciprocalExpression)recipGoal.get().getGoal();
                expression.setPartnerExternal(wolf);
            }
            return potentialPlaymate;
        }
        play.tick();
        return null;
    }
    
    private void chase(){
        changeState(1);
        if(wolf.getNavigator().hasPath()){
            position = RandomPositionGenerator.findRandomTargetBlockAwayFrom(wolf,20,5,partner.getPositionVec());
            if(position != null){
                Path path = wolf.getNavigator().getPathToPos(position.x,position. y, position.z, 0);
                wolf.getNavigator().setPath(path, 1);
            }
        }
    }

    private void annoy(){
        changeState(2);
        controller.jumpTowards(partner);
    }

    private void runAround(){
        changeState(3);
        if(!wolf.getNavigator().hasPath()){
            if(flipX){
                x = -x;
            } else {
                z = -z;
            }
            flipX = !flipX;
            wolf.getNavigator().tryMoveToXYZ(
                partner.getPositionVec().x + 2 * x,
                partner.getPositionVec().y,
                partner.getPositionVec().z + 2 * z, 1);
        }
    }

    private void setPartnerPlayType(){
        if(partner != null && partner instanceof WolfEntity){
            WolfEntity partnerWolf = (WolfEntity)partner;
            ReciprocalExpression recipExpr = (ReciprocalExpression)getWolfReciprocalExpression(partnerWolf).get().getGoal();
            recipExpr.arbitraryState = partnerState;

        }
    }

    private Optional<PrioritizedGoal> getWolfReciprocalExpression(WolfEntity otherWolf){
        return otherWolf.goalSelector.getRunningGoals().filter((goal) -> {
            return goal.getGoal() instanceof ReciprocalExpression;
        }).findFirst();
    }

    private boolean isPlaying(Expressions expression){
        return (expression instanceof PlayfulExpression || expression instanceof ReciprocalExpression) && expression.isActive();
    }
}
