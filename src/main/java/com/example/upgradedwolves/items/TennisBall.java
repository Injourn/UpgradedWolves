package com.example.upgradedwolves.items;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.entities.TennisBallEntity;
import com.example.upgradedwolves.init.ModEntities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class TennisBall extends Item {

    static private final int TENNIS_BALL_STACK_SIZE = 8;

    public TennisBall() {
        super(new Item.Properties().stacksTo(TENNIS_BALL_STACK_SIZE));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {        
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        Player playerIn = (Player)entityLiving;
        int time = getUseDuration(stack, playerIn) - timeLeft;
        float bonus = Math.max(0,Math.min(15,time/3)) / 10.0F;
        worldIn.playSound((Player)null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isClientSide) {
            TennisBallEntity tennisBallEntity = new TennisBallEntity(ModEntities.tennisBallEntityType,playerIn,worldIn);
            //tennisBallEntity.setItem(stack);
            tennisBallEntity.setOwner(playerIn);
            tennisBallEntity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 0.5F + bonus, 1.0F);
            worldIn.addFreshEntity(tennisBallEntity);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.isCreative()) {
            stack.shrink(1);
        }
    }
}
