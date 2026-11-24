package com.example.upgradedwolves.entities.plushy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.example.upgradedwolves.common.TrainingEventHandler;
import com.example.upgradedwolves.init.ModEntities;
import com.example.upgradedwolves.itemHandler.WolfToysHandler;
import com.example.upgradedwolves.items.MobPlushy;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;

public class MobPlushyEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<ItemStack> ITEMSTACK_DATA = SynchedEntityData.defineId(ThrowableProjectile.class, EntityDataSerializers.ITEM_STACK);
    private boolean inGround;
    @Nullable
    private BlockState inBlockState;

    public MobPlushyEntity(EntityType<? extends MobPlushyEntity> p_i50159_1_, Level p_i50159_2_) {
        super(p_i50159_1_, p_i50159_2_);
         
    }

    public MobPlushyEntity(Level worldIn, LivingEntity throwerIn) {
        super(ModEntities.mobPlushyEntityType, throwerIn, worldIn);
    }

    public MobPlushyEntity(Level worldIn, double x, double y, double z) {
        super(ModEntities.mobPlushyEntityType, x, y, z, worldIn);
    }    

    @Override
    protected void onHit(HitResult result) {
        HitResult.Type raytraceresult$type = result.getType();
        if(raytraceresult$type == HitResult.Type.ENTITY){
            EntityHitResult entityResult = (EntityHitResult)result;        
            if(entityResult.getEntity() instanceof Wolf){
                TrainingEventHandler.wolfCollectEntity(this, (Wolf)entityResult.getEntity(), getItem());
            }
            else if(entityResult.getEntity() instanceof Player){
            }
        }
        if (raytraceresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockResult = (BlockHitResult)result;
            if(blockResult.getDirection().getAxis() == Direction.Axis.Y)
                this.OnHitBlock(blockResult);
            else{
                Vec3 vector3d1 = this.getDeltaMovement();
                this.setDeltaMovement(
                    blockResult.getDirection().getAxis() == Direction.Axis.X ? -vector3d1.x * .3 : vector3d1.x * .5,
                    vector3d1.y * .5,
                    blockResult.getDirection().getAxis() == Direction.Axis.Z ? -vector3d1.z * .3 : vector3d1.z * .5
                );
            }

        }
    }

    @Override
    public void playerTouch(Player entityIn) {        
        if (!this.level().isClientSide) {
            boolean flag = this.getOwner().getUUID() == entityIn.getUUID() && tickCount > 20;
            if (flag) {                
                if(!entityIn.isCreative() && !entityIn.addItem(getItem()))
                    flag = false;
            }
    
            if (flag) {                
                entityIn.take(this, 1);
                this.kill();
            }
    
        }
    }

    public void onCollideWithWolf(Wolf wolf){
        wolf.setItemInHand(InteractionHand.MAIN_HAND, getItem());
        this.kill(); 
    }

    protected List<MobPlushy> plushTypes(){
        ArrayList<MobPlushy> plushes = new ArrayList<MobPlushy>();        
        plushes.add(WolfToysHandler.CREEPER_PLUSHY);
        plushes.add(WolfToysHandler.SKELETON_PLUSHY);
        plushes.add(WolfToysHandler.ZOMBIE_PLUSHY);
        return plushes;
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.func_213882_k();
        return itemstack.isEmpty() ? null : itemstack;
    }

    protected ItemStack func_213882_k() {
        return this.getEntityData().get(ITEMSTACK_DATA);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return ForgeHooks.getEntitySpawnPacket(this);
    }

    @Override
    public void tick() {
        if(getItem() == null)
            this.kill();
        super.tick();
        Vec3 blockpos = this.getPosition(1);
        BlockState blockstate = this.level().getBlockState(BlockPos.containing(blockpos));
        if (!blockstate.isAir()) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), BlockPos.containing(blockpos));
            if (!voxelshape.isEmpty()) {
                Vec3 vector3d1 = this.getPosition(1);

                for(AABB axisalignedbb : voxelshape.toAabbs()) {
                    if (axisalignedbb.expandTowards(blockpos).contains(vector3d1)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        for(Wolf wolf : this.level().getEntitiesOfClass(Wolf.class, this.getBoundingBox())) {
            onCollideWithWolf(wolf);    
        }
        
        if (this.inGround) {
            if (this.inBlockState != blockstate && this.stillInGround()) {
               this.notInBlock();
            }        
        }
    }

    private boolean stillInGround(){
        return this.inGround && this.level().noCollision((new AABB(this.getPosition(1), this.getPosition(1))).inflate(0.06D,0.06D,0.06D));
    }

    private void notInBlock() {
        this.inGround = false;
        Vec3 vector3d = this.getDeltaMovement();
        this.setDeltaMovement(vector3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
    }

    protected void OnHitBlock(BlockHitResult p_230299_1_) {
        this.inBlockState = this.level().getBlockState(p_230299_1_.getBlockPos());
        super.onHitBlock(p_230299_1_);
        Vec3 vector3d = p_230299_1_.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vector3d);
        Vec3 vector3d1 = vector3d.normalize().scale((double)0.05F);
        this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
        this.inGround = true;
    }

    public void setItem(ItemStack stack) {
        if (plushTypes().contains(stack.getItem()) || stack.getComponents().has(DataComponents.CUSTOM_DATA)) {
           this.getEntityData().set(ITEMSTACK_DATA, Util.make(stack.copy(), (p_213883_0_) -> {
              p_213883_0_.setCount(1);
           }));
        }
  
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return super.shouldRender(x, y, z);
        //return true;
    }

    public void writeAdditional(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ItemStack itemstack = this.func_213882_k();
        if (!itemstack.isEmpty()) {
           compound.put("Item", itemstack.save(this.registryAccess(), new CompoundTag()));
        }
  
    }
  
    public void readAdditional(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ItemStack itemstack = ItemStack.parseOptional(this.registryAccess(), compound.getCompound("Item"));
        this.setItem(itemstack);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ITEMSTACK_DATA, ItemStack.EMPTY); 
        
    }
}
