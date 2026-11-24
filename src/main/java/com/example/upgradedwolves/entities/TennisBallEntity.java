package com.example.upgradedwolves.entities;

import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.common.WolfPlayerInteraction;
import com.example.upgradedwolves.entities.goals.WolfFindAndPickUpItemGoal;
import com.example.upgradedwolves.init.ModEntities;
import com.example.upgradedwolves.itemHandler.WolfToysHandler;

import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

public class TennisBallEntity extends WolfChaseableEntity implements IEntityAdditionalSpawnData {

    public TennisBallEntity(Level worldIn) {
        super(ModEntities.tennisBallEntityType,worldIn);
    }

    public TennisBallEntity(EntityType<? extends TennisBallEntity> p_i50159_1_, Level p_i50159_2_) {
        super(p_i50159_1_, p_i50159_2_);
    }

    public TennisBallEntity(EntityType<? extends TennisBallEntity> p_i50159_1_,LivingEntity entity, Level p_i50159_2_){
        super(p_i50159_1_,entity,p_i50159_2_);
    }

    @Override
    protected Item getDefaultItem() {
        return WolfToysHandler.TENNIS_BALL;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {        
        return ForgeHooks.getEntitySpawnPacket(this);
    }

    @Override
    protected void onHit(HitResult result) {
        if(result.getType() == HitResult.Type.BLOCK){
            BlockHitResult blockResult = (BlockHitResult)result;
            Vec3 vector3d1 = this.getDeltaMovement();
            if(this.getDeltaMovement().length() > 0.2)
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), level().getBlockState(blockResult.getBlockPos()).getBlock().getSoundType(null, null, null, null).getPlaceSound(), SoundSource.BLOCKS, 0.5F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F, false);
            if(blockResult.getDirection().getAxis() == Direction.Axis.Y && this.getDeltaMovement().length() < 0.1)
                super.OnHitBlock(blockResult);
            else
                this.setDeltaMovement(
                    blockResult.getDirection().getAxis() == Direction.Axis.X ? -vector3d1.x * .7 : vector3d1.x * .9,
                    blockResult.getDirection().getAxis() == Direction.Axis.Y ? -vector3d1.y * .7 : vector3d1.y * .9,
                    blockResult.getDirection().getAxis() == Direction.Axis.Z ? -vector3d1.z * .7 : vector3d1.z * .9
                    );
            HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canCollideWith);
            if(raytraceresult.getType() != HitResult.Type.MISS){
                onHit(raytraceresult);
            }
        }
        if(result.getType() == HitResult.Type.ENTITY){
            EntityHitResult entityResult = (EntityHitResult)result;
            if(speedFactor(1)){
                if(entityResult.getEntity() instanceof LivingEntity){
                    LivingEntity entity = (LivingEntity)entityResult.getEntity();
                    if(this.getOwner() != null) {
                        entity.hurt(/*DamageSource.playerAttack*/((Player)this.getOwner()).damageSources().thrown(this, this.getOwner()), 1);
                    }
                    double speed = this.getDeltaMovement().length() * .6;
                    Vec3 bounceDirection = new Vec3(entity.getPosition(1).x - this.getPosition(1).x,
                                                                entity.getPosition(1).y - this.getPosition(1).y,
                                                                entity.getPosition(1).z - this.getPosition(1).z)
                                                                .normalize();
                    this.setDeltaMovement(bounceDirection.scale(speed));
                }
            }            
            else if(entityResult.getEntity() instanceof Wolf){
                wolfCollect((Wolf)entityResult.getEntity());
            }
        }

    }
    
    public void tick() {
        super.tick();
    }

    public void wolfCollect(Wolf wolf){        
        IWolfStats handler = WolfStatsHandler.getHandler(wolf);
        ItemStack tennisBallItem = getItem();
        int wolfSlot = handler.getInventory().getAvailableSlot(tennisBallItem);

        if(wolfSlot >= 0){
            handler.getInventory().insertItem(wolfSlot, tennisBallItem, false);
            WolfFindAndPickUpItemGoal goal = (WolfFindAndPickUpItemGoal)WolfPlayerInteraction.getWolfGoal(wolf, WolfFindAndPickUpItemGoal.class);
            if(goal != null)
                goal.setEndPoint(wolf.getPosition(1));
            this.kill();
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {

    }
}
