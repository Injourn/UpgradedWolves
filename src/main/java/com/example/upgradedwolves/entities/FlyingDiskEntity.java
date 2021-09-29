package com.example.upgradedwolves.entities;

import com.example.upgradedwolves.common.TrainingEventHandler;
import com.example.upgradedwolves.init.ModEntities;
import com.example.upgradedwolves.itemHandler.WolfToysHandler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import com.mojang.math.Vector3d;
import net.minecraft.util.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FlyingDiskEntity extends WolfChaseableEntity{

    public int timeOut = 0;
    protected int flightTime = 100;
    protected float variant;
    protected boolean fly = true;

    public FlyingDiskEntity(EntityType<? extends FlyingDiskEntity> p_i50159_1_, World p_i50159_2_) {
        super(p_i50159_1_, p_i50159_2_);
    }

    public FlyingDiskEntity(World worldIn, LivingEntity throwerIn) {
        super(ModEntities.flyingDiskEntityType, throwerIn, worldIn);
    }

    public FlyingDiskEntity(World worldIn, double x, double y, double z) {
        super(ModEntities.flyingDiskEntityType, x, y, z, worldIn);
    }

    @Override
    protected Item getDefaultItem() {
        return WolfToysHandler.FLYINGDISK;
    }

    @Override
    public IPacket<?> createSpawnPacket() {        
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if(result.getType() == RayTraceResult.Type.BLOCK){
            this.timeOut += flightTime * variant;
            BlockRayTraceResult blockResult = (BlockRayTraceResult)result;
            if(this.getDeltaMovement().length() > 0.2)
                this.world.playSound(this.getX(), this.getY(), this.getZ(), world.getBlockState(blockResult.getPos()).getBlock().getSoundType(null,null,null,null).getPlaceSound(), SoundCategory.BLOCKS, 0.3F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
            Vector3d vector3d1 = this.getDeltaMovement();
            if(blockResult.getFace().getAxis() == Direction.Axis.Y && this.getDeltaMovement().length() < 0.1)
                super.OnHitBlock(blockResult);
            else
                this.setDeltaMovement(
                    blockResult.getFace().getAxis() == Direction.Axis.X ? -vector3d1.x * .2 : vector3d1.x * .3,
                    blockResult.getFace().getAxis() == Direction.Axis.Y ? -vector3d1.y * .2 : vector3d1.y * .3,
                    blockResult.getFace().getAxis() == Direction.Axis.Z ? -vector3d1.z * .2 : vector3d1.z * .3
                    );
            RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
            if(raytraceresult.getType() != RayTraceResult.Type.MISS){
                onImpact(raytraceresult);
            }
        }
        if(result.getType() == RayTraceResult.Type.ENTITY){
            EntityRayTraceResult entityResult = (EntityRayTraceResult)result;
            if(speedFactor(1)){
                if(entityResult.getEntity() instanceof LivingEntity){
                    LivingEntity entity = (LivingEntity)entityResult.getEntity();
                    entity.attackEntityFrom(DamageSource.causePlayerDamage((Player)this.func_234616_v_()), 1);
                    double speed = this.getDeltaMovement().length() * .6;
                    Vector3d bounceDirection = new Vector3d(entity.getPositionVec().x - this.getPositionVec().x,
                                                                entity.getPositionVec().y - this.getPositionVec().y,
                                                                entity.getPositionVec().z - this.getPositionVec().z)
                                                                .normalize();
                    this.setDeltaMovement(bounceDirection.scale(speed));
                }
            }            
            else if(entityResult.getEntity() instanceof Wolf){                
                TrainingEventHandler.wolfCollectEntity(this, (Wolf)entityResult.getEntity(), new ItemStack(getDefaultItem()));
            }
        }

    }
    
    public void tick() {
        super.tick();
        Player player = (Player)func_234616_v_();
        if(fly && player != null){
            Vector3d retrieveDirection = new Vector3d(player.getPositionVec().x - this.getPositionVec().x,
                                                                    player.getPositionVec().y + 1.5 - this.getPositionVec().y,
                                                                    player.getPositionVec().z - this.getPositionVec().z)
                                                                    .normalize().scale(.01);
            this.addVelocity(retrieveDirection.x, retrieveDirection.y,retrieveDirection.z);
            if(timeOut++ >= flightTime * variant){
                fly = false;
                this.setNoGravity(false);
            }
        }
    }

    @Override
    protected float getGravityVelocity() {      
        return 0.03f;
    }
    @Override
    public void func_234612_a_(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_,
            float p_234612_5_, float p_234612_6_) {  
        super.func_234612_a_(p_234612_1_, p_234612_2_, p_234612_3_, p_234612_4_, p_234612_5_, p_234612_6_);
        variant = (p_234612_5_ - .2f) * 2.5f;
    }
}