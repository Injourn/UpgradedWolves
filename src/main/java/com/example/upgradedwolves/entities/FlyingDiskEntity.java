package com.example.upgradedwolves.entities;

import com.example.upgradedwolves.common.TrainingEventHandler;
import com.example.upgradedwolves.itemHandler.WolfToysHandler;

import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

public class FlyingDiskEntity extends WolfChaseableEntity implements IEntityAdditionalSpawnData{

    public int timeOut = 0;
    protected int flightTime = 100;
    protected float variant;
    protected boolean fly = true;

    public FlyingDiskEntity(EntityType<? extends FlyingDiskEntity> p_i50159_1_, Level p_i50159_2_) {
        super(p_i50159_1_, p_i50159_2_);
    }

    
    public FlyingDiskEntity(EntityType<? extends FlyingDiskEntity> flyingDiskEntityType, Player playerIn, Level worldIn) {
        super(flyingDiskEntityType,playerIn,worldIn);
    }


    @Override
    protected Item getDefaultItem() {
        return WolfToysHandler.FLYING_DISK;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {        
        return ForgeHooks.getEntitySpawnPacket(this);
    }

    @Override
    protected void onHit(HitResult result) {
        if(result.getType() == HitResult.Type.BLOCK){
            this.timeOut += flightTime * variant;
            BlockHitResult blockResult = (BlockHitResult)result;
            if(this.getDeltaMovement().length() > 0.2)
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), level().getBlockState(blockResult.getBlockPos()).getBlock().getSoundType(null,null,null,null).getPlaceSound(), net.minecraft.sounds.SoundSource.BLOCKS, 0.3F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F, false);
            Vec3 vector3d1 = this.getDeltaMovement();
            if(blockResult.getDirection().getAxis() == Direction.Axis.Y && this.getDeltaMovement().length() < 0.1)
                super.OnHitBlock(blockResult);
            else
                this.setDeltaMovement(
                    blockResult.getDirection().getAxis() == Direction.Axis.X ? -vector3d1.x * .2 : vector3d1.x * .3,
                    blockResult.getDirection().getAxis() == Direction.Axis.Y ? -vector3d1.y * .2 : vector3d1.y * .3,
                    blockResult.getDirection().getAxis() == Direction.Axis.Z ? -vector3d1.z * .2 : vector3d1.z * .3
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
                TrainingEventHandler.wolfCollectEntity(this, (Wolf)entityResult.getEntity(), getItem());
            }
        }

    }
    
    public void tick() {
        super.tick();
        Player player = (Player)getOwner();
        if(fly && player != null){
            Vec3 retrieveDirection = new Vec3(player.getPosition(1).x - this.getPosition(1).x,
                                                                    player.getPosition(1).y + 1.5 - this.getPosition(1).y,
                                                                    player.getPosition(1).z - this.getPosition(1).z)
                                                                    .normalize().scale(.01);
            this.push(retrieveDirection.x, retrieveDirection.y,retrieveDirection.z);
            if(timeOut++ >= flightTime * variant){
                fly = false;
                this.setNoGravity(false);
                
            }
        }
    }
    
    @Override
    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_,
            float p_234612_5_, float p_234612_6_) {  
        super.shootFromRotation(p_234612_1_, p_234612_2_, p_234612_3_, p_234612_4_, p_234612_5_, p_234612_6_);
        variant = (p_234612_5_ - .2f) * 2.5f;
    }


    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        
    }


    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        
    }
}