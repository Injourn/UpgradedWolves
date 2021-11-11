package com.example.upgradedwolves.personality.expressions;

import com.example.upgradedwolves.personality.Behavior;
import com.example.upgradedwolves.personality.CommonActionsController;

import net.minecraft.world.entity.LivingEntity;
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


    public PlayfulExpression(Wolf wolf, Behavior subBehavior) {
        super(wolf, subBehavior);
        maxEngagement = 300;
        controller = new CommonActionsController(wolf);
    }

    @Override
    public void stop(){
        x = 1;
        z = 1;
    }

    @Override
    public void tick(){

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
        engagement = 300;
    }

    @Override
    protected LivingEntity searchForPartner() {        
        return getAnotherWolfOrOwner();
    }
    
    private void chase(){
        if(wolf.getNavigation().isInProgress()){
            position = DefaultRandomPos.getPosAway(wolf,20,5,partner.getPosition(1));
            if(position != null){
                Path path = wolf.getNavigation().createPath(position.x,position. y, position.z, 0);
                wolf.getNavigation().moveTo(path, 1);
            }
        }
    }

    private void annoy(){
        controller.jumpTowards(partner);
    }

    private void runAround(){
        if(!wolf.getNavigation().isInProgress()){
            if(flipX){
                x = -x;
            } else {
                z = -z;
            }
            flipX = !flipX;
        }
    }
}