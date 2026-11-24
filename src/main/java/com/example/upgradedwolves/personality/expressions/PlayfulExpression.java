package com.example.upgradedwolves.personality.expressions;

import java.util.List;
import java.util.Optional;

import com.example.upgradedwolves.personality.Behavior;
import com.example.upgradedwolves.personality.CommonActionsController;
import com.example.upgradedwolves.utils.RandomRangeTimer;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class PlayfulExpression extends Expressions {
    protected CommonActionsController controller;
    protected Vec3 position;
    protected int x = 1;
    protected int z = 1;
    protected boolean flipX;
    protected RandomRangeTimer play;
    protected boolean available;
    protected int partnerState;


    public PlayfulExpression(Wolf wolf, Behavior subBehavior) {
        super(wolf, subBehavior);
        maxEngagement = 300;
        controller = new CommonActionsController(wolf);
        //play = new RandomRangeTimer(1200,4800,wolf.getRandom());
        play = new RandomRangeTimer(100,100,wolf.getRandom());
        play.setFunction(() -> {available = true;});
    }

    @Override
    public void stop(){
        x = 1;
        z = 1;
    }

    @Override
    public void tick(){
        super.tick();
        if(partner instanceof Wolf){
            //get other wolf's behavior to determine play type.
        }
        subBehaviorTick();
        setPartnerPlayType();
    }

    @Override
    protected void subBehaviorTick() {
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
            case EMPTY:
                break;
        }
    }
    @Override
    public void reciprocateAction(Wolf otherWolf){
        if(arbitraryState == 1){
            otherWolf.getMoveControl().setWantedPosition(wolf.getX(), wolf.getY(), wolf.getZ(), 1);
        } else {
            otherWolf.getLookControl().setLookAt(wolf);
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
            if(potentialPlaymate instanceof Wolf){
                Wolf wolfPlaymate = (Wolf)potentialPlaymate;

                List<WrappedGoal> excludeGoals = wolfPlaymate.goalSelector.getAvailableGoals().stream().filter((goal) -> {
                    return goal.getGoal() instanceof Expressions && isPlaying((Expressions)goal.getGoal());
                }).toList();
                if(excludeGoals.size() > 0)
                    return null;

                wolfPlaymate.goalSelector.getAvailableGoals().stream().filter((goal) -> {
                    return goal.getGoal() instanceof Expressions;
                }).toList().forEach(x -> x.stop());
                Optional<WrappedGoal> recipGoal = getWolfReciprocalExpression(wolfPlaymate);
                if(!recipGoal.isPresent()){
                    return null;
                }
                recipGoal.get().start();
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
        if(wolf.getNavigation().isInProgress()){
            position = DefaultRandomPos.getPosAway(wolf,20,5,partner.getPosition(1));
            if(position != null){
                Path path = wolf.getNavigation().createPath(position.x,position. y, position.z, 0);
                wolf.getNavigation().moveTo(path, 1);
            }
        }
    }

    private void annoy(){
        changeState(2);
        controller.jumpTowards(partner);
    }

    private void runAround(){
        changeState(3);
        if(!wolf.getNavigation().isInProgress()){
            if(flipX){
                x = -x;
            } else {
                z = -z;
            }
            flipX = !flipX;
            wolf.getNavigation().moveTo(
                partner.getPosition(0).x + 2 * x,
                partner.getPosition(0).y,
                partner.getPosition(0).z + 2 * z, 1);
        }
    }

    private void setPartnerPlayType(){
        if(partner != null && partner instanceof Wolf){
            Wolf partnerWolf = (Wolf)partner;
            ReciprocalExpression recipExpr = (ReciprocalExpression)getWolfReciprocalExpression(partnerWolf).get().getGoal();
            recipExpr.arbitraryState = partnerState;

        }
    }

    private Optional<WrappedGoal> getWolfReciprocalExpression(Wolf otherWolf){
        return otherWolf.goalSelector.getAvailableGoals().stream().filter((goal) -> {
            return goal.getGoal() instanceof ReciprocalExpression;
        }).findFirst();
    }

    private boolean isPlaying(Expressions expression){
        return (expression instanceof PlayfulExpression || expression instanceof ReciprocalExpression) && expression.isActive();
    }
}
