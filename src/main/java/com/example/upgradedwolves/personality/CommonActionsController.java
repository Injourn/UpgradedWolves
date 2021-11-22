package com.example.upgradedwolves.personality;

import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;

public class CommonActionsController {
    public final WolfEntity wolf;

    public CommonActionsController(WolfEntity wolf){
        this.wolf = wolf;
    }

    public void bark(){
        wolf.playAmbientSound();
    }

    public void growl(){
        wolf.playSound(SoundEvents.ENTITY_WOLF_GROWL, 1, 1);
    }

    public void jump(){
        if(wolf.isOnGround()){
            wolf.getJumpController().setJumping();;
        }
    }

    public void whine(){
        wolf.playSound(SoundEvents.ENTITY_WOLF_WHINE, 1, .85f + (wolf.getRNG().nextFloat() * .3f));
    }

    public void jumpTowards(LivingEntity entity){
        if(wolf.isOnGround()){
            Vector3d vector3d = wolf.getMotion();
            Vector3d vector3d1 = new Vector3d(entity.getPosX() - wolf.getPosX(), 0.0D, entity.getPosZ() - wolf.getPosZ());
            if (vector3d1.lengthSquared() > 1.0E-7D) {
            vector3d1 = vector3d1.normalize().scale(0.4D).add(vector3d.scale(0.2D));
            }

            entity.setMotion(vector3d1.x, 0.4, vector3d1.z);
        }
    }

    public void jumpTowards(Vector3d position){
        if(wolf.isOnGround()){
            Vector3d vector3d = wolf.getMotion();
            Vector3d vector3d1 = new Vector3d(position.x - this.wolf.getPosX(), 0.0D, position.z - this.wolf.getPosZ());
            if (vector3d1.lengthSquared() > 1.0E-7D) {
                vector3d1 = vector3d1.normalize().scale(0.4D).add(vector3d.scale(0.2D));
            }

            this.wolf.setMotion(vector3d1.x, 0.4D, vector3d1.z);
        }
    }

    public Vector3d getLeft(){
        Vector3d angle = wolf.getLookVec();
        return new Vector3d(-angle.z,0,angle.x).add(wolf.getPositionVec());
    }

    public Vector3d getRight(){
        Vector3d angle = wolf.getLookVec();
        return new Vector3d(angle.z,0,angle.x).add(wolf.getPositionVec());
    }

    public void jumpLateral(){
        Vector3d angle;
        if(wolf.getRNG().nextBoolean()){
            angle = getLeft();
        } else {
            angle = getRight();
        }
        jumpTowards(angle);
    }
}
