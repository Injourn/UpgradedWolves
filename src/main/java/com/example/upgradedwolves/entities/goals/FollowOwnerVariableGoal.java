package com.example.upgradedwolves.entities.goals;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.common.WolfPlayerInteraction;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class FollowOwnerVariableGoal extends FollowOwnerGoal{
    TamableAnimal tameable;
    float dist;
    PathNavigation navigator;

    public FollowOwnerVariableGoal(TamableAnimal tameable, double speed, float minDist, float maxDist) {
        super(tameable, speed, minDist, maxDist);
        this.tameable = tameable;
        this.dist = minDist;
        this.navigator = tameable.getNavigation();
    }

    public void setMinDistance(float dist){
        this.dist = dist;
    }

    public boolean canUse() {
        if(tameable.getOwner() == null)
            return false;
        else if(this.tameable.distanceToSqr(tameable.getOwner()) < (double)(this.dist * this.dist))
            return false;
        else if(this.tameable instanceof Wolf){
            Wolf wolf = (Wolf)tameable;
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            if(WolfPlayerInteraction.getWolfGoal((Wolf)tameable,WolfFindAndPickUpItemGoal.class) != null || handler.getRoamPoint() != null)
                return false;
            else return true;
        }        
        else
            return super.canUse();
    }
    
    @Override
    public boolean canContinueToUse() {
        if (this.navigator.isDone()) {
           return false;
        } else if (this.tameable.isInSittingPose()) {
           return false;
        } else {
           return !(this.tameable.distanceToSqr(tameable) <= (double)(this.dist * this.dist));
        }
     }

    private int getRandomNumber(int min, int max) {
        return this.tameable.getRandom().nextInt(max - min + 1) + min;
    }

    void teleportToEntity(){
        Vec3 blockpos = this.tameable.getOwner().getPosition(1);

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomNumber(-3, 3);
            int k = this.getRandomNumber(-1, 1);
            int l = this.getRandomNumber(-3, 3);
            boolean flag = this.tryToTeleportToLocation(blockpos.x() + j, blockpos.y() + k, blockpos.z() + l);
            if (flag) {
                return;
            }
        }
    }

    private boolean tryToTeleportToLocation(double x, double y, double z) {
        LivingEntity owner = tameable.getOwner();
        if (Math.abs((double)x - owner.getX()) < 2.0D && Math.abs((double)z - owner.getZ()) < 2.0D) {
           return false;
        } else if (!this.isTeleportFriendlyBlock(BlockPos.containing(x, y, z))) {
           return false;
        } else {
           this.tameable.lerpTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.tameable.getYRot(), this.tameable.getXRot(),1);
           this.navigator.stop();
           return true;
        }
    }
    private boolean isTeleportFriendlyBlock(BlockPos pos) {
        Level world = tameable.level();
        PathType blockpathtypes = WalkNodeEvaluator.getPathTypeStatic(tameable, pos);
        if (blockpathtypes != PathType.WALKABLE) {
           return false;
        } else {
           BlockState blockstate = world.getBlockState(pos);
           if (blockstate.getBlock() instanceof LeavesBlock) {
              return false;
           } else {
              BlockPos blockpos = pos.subtract(this.tameable.blockPosition());
              return world.noCollision(this.tameable, this.tameable.getBoundingBox().move(blockpos));
           }
        }
     }

    @Override
    public void tick() {
        LivingEntity owner = tameable.getOwner();
        this.tameable.getLookControl().setLookAt(owner, 10.0F, (float)this.tameable.getMaxHeadXRot());
            
         if (!this.tameable.isLeashed() && !tameable.isPassenger()) {
            if (this.tameable.distanceToSqr(owner) >= 900.0D * 25) {
               this.teleportToEntity();
            } else {
               this.navigator.moveTo(owner, 1);
            }
        }
    }
}


