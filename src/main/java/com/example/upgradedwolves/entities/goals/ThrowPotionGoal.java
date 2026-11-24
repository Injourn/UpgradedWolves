package com.example.upgradedwolves.entities.goals;

import java.util.List;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.entities.utilities.AbilityEnhancer;
import com.example.upgradedwolves.entities.utilities.EntityFinder;
import com.example.upgradedwolves.itemHandler.WolfItemStackHandler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import java.util.ArrayList;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class ThrowPotionGoal extends Goal {
    protected final Wolf wolf;
    protected final IWolfStats handler;
    protected LivingEntity target;
    protected EntityFinder<LivingEntity> allyFinder;
    protected int slot;

    public ThrowPotionGoal(Wolf wolf){
        this.wolf = wolf;
        this.handler = WolfStatsHandler.getHandler(wolf);
        allyFinder = new EntityFinder<LivingEntity>(this.wolf,LivingEntity.class);
    }

    @Override
    public boolean canUse() {
        WolfItemStackHandler wolfItems = handler.getInventory();
        int bonus = AbilityEnhancer.increaseEveryLevel(wolf, 10, 3);
        List<LivingEntity> allyList = allyFinder.findWithPredicate(7+ bonus, 3 + bonus, ally ->
        (ally == wolf.getOwner() || (ally instanceof TamableAnimal && ((TamableAnimal)ally).getOwner() == wolf.getOwner()) &&
        (ally.isOnFire() || ally.getHealth() < 8)));
        for (LivingEntity livingEntity : allyList) {
            int slot = -1;
            if(livingEntity.isOnFire() && !livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)){
                slot = burningQuestion(wolfItems,MobEffects.FIRE_RESISTANCE);
            }
            if(livingEntity.getHealth() < 8){
                slot = burningQuestion(wolfItems, MobEffects.HEALTH_BOOST);
                if(slot < 0)
                    slot = burningQuestion(wolfItems, MobEffects.REGENERATION);
            }
            if(slot >= 0){
                this.slot = slot;
                target = livingEntity;
                break;
            }
        }
        return target != null;
    }

    @Override
    public void start() {        
        super.start();
        ItemStack potionStack = handler.getInventory().extractItem(slot, 1, false);
        WolfThrowPotion(potionStack);
        target = null;
    }

    //Gets a potion with a certain effect
    private int burningQuestion(WolfItemStackHandler wolfItems,Holder<MobEffect> effectType){
        return wolfItems.getArbitraryItemStack(x -> x.getItem() instanceof ThrowablePotionItem && remainingWithEffect(x,effectType));
    }

    private boolean remainingWithEffect(ItemStack stack, Holder<MobEffect> effectType){
        if(stack.getItem() instanceof ThrowablePotionItem){
            PotionContents potioncontents = stack.getComponents().get(DataComponents.POTION_CONTENTS);
            if(potioncontents != null){
                List<MobEffectInstance> effects = new ArrayList<MobEffectInstance>();
                potioncontents.getAllEffects().forEach(effects::add);
                effects.removeIf(y -> !y.getEffect().is(effectType));
                return effects.size() > 0;
            }
        }
        return false;
    }

    private void WolfThrowPotion(ItemStack potionStack){
        Vec3 vector3d = target.getDeltaMovement();
        double d0 = target.getX() + vector3d.x - wolf.getX();
        double d1 = target.getEyeY() - (double)1.1F - wolf.getY();
        double d2 = target.getZ() + vector3d.z - wolf.getZ();
        float f = Mth.sqrt((float)(d0 * d0 + d2 * d2));

        ThrownPotion potionentity = new ThrownPotion(wolf.level(), wolf);
        potionentity.setItem(potionStack);
        potionentity.setXRot(potionentity.getXRot() - 20.0F);
        potionentity.shoot(d0, d1 + (double)(f * 0.2F), d2, 0.75F, 8.0F);
        if (!wolf.isSilent()) {
            wolf.level().playSound((Player)null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.SPLASH_POTION_THROW, wolf.getSoundSource(), 1.0F, 0.8F + wolf.getRandom().nextFloat() * 0.4F);
        }
        wolf.level().addFreshEntity(potionentity);             
    }
    
}
