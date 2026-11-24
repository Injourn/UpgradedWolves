package com.example.upgradedwolves.entities.goals;

import java.util.List;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.entities.utilities.AbilityEnhancer;
import com.example.upgradedwolves.entities.utilities.EntityFinder;

import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class WolfTossArrowGoal extends CoolDownGoal {
    protected final Wolf wolf;
    protected final EntityFinder<Monster> entityFinder;
    protected Monster target;
    protected int arrowStackSlot;
    int windUpTicks = 10;


    public WolfTossArrowGoal(Wolf wolf){
        this.wolf = wolf;
        this.entityFinder = new EntityFinder<Monster>(wolf,Monster.class);
        setCoolDownInSeconds(300);
    }

    @Override
    public boolean canUse() {
        if(active() && wolf.getTarget() != null){
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            int slot = handler.getInventory().getArbitraryItem(item -> item instanceof net.minecraft.world.item.ArrowItem);
            if(slot >= 0){
                List<Monster> enemies = entityFinder.findWithPredicate(5, 3,enemy -> (!(enemy instanceof NeutralMob) || ((NeutralMob)enemy).getTarget() != null) && wolf.getSensing().hasLineOfSight(enemy));
                if(enemies.size() > 0){
                    target = enemies.get(0);
                    arrowStackSlot = slot;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        wolf.getJumpControl().jump();
    }

    @Override
    public boolean canContinueToUse() {
        if(windUpTicks-- <= 0){
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            ItemStack arrowStack = handler.getInventory().getStackInSlot(arrowStackSlot);
            if(arrowStack.getItem() instanceof ArrowItem)
                attackEntityWithRangedAttack(target, 2 + handler.getWolfStrength(), arrowStack);
            startCoolDown(AbilityEnhancer.minMaxIncrease(wolf, 200, 5, 150));
            windUpTicks = 30;
            return false;
        }        
        wolf.setYRot(wolf.getYRot() + 1);
        return false;
    }

    private void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor,ItemStack arrowStack) {
        AbstractArrow abstractarrowentity = ProjectileUtil.getMobArrow(wolf, arrowStack, distanceFactor, arrowStack);
        double d0 = target.getX() - wolf.getX();
        double d1 = target.getY() - abstractarrowentity.getY();
        double d2 = target.getZ() - wolf.getZ();
        double d3 = (double)Mth.sqrt((float)(d0 * d0 + d2 * d2));
        abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - wolf.level().getDifficulty().getId() * 4));
        wolf.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (wolf.getRandom().nextFloat() * 0.4F + 0.8F));
        wolf.level().addFreshEntity(abstractarrowentity);
     }
}
