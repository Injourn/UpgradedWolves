package com.example.upgradedwolves.items;

import java.util.concurrent.Callable;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.entities.plushy.CreeperPlushyModel;
import com.example.upgradedwolves.entities.plushy.MobPlushyEntity;
import com.example.upgradedwolves.entities.plushy.SkeletonPlushyModel;
import com.example.upgradedwolves.entities.plushy.TilePlushyEntity;
import com.example.upgradedwolves.entities.plushy.ZombiePlushyModel;
import com.example.upgradedwolves.utils.MobPlushyType;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

public class MobPlushy extends Item {
    public MobPlushyType plushType;

    public MobPlushy(String registryName, MobPlushyType type) {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC).setISTER(() -> tilePlushyISTER()));
        this.setRegistryName(UpgradedWolves.getId(registryName));
        plushType = type;
    }

    @OnlyIn(Dist.CLIENT)
    private static Callable<ItemStackTileEntityRenderer> tilePlushyISTER(){
        return TilePlushyEntity::new;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {        
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);              
        if (!worldIn.isRemote) {
            MobPlushyEntity mobPlushyEntity = new MobPlushyEntity(worldIn, playerIn);
            mobPlushyEntity.setItem(itemstack);
            mobPlushyEntity.setShooter(playerIn);
            mobPlushyEntity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 0.5F, 1.0F);
            worldIn.addEntity(mobPlushyEntity);
        }

        playerIn.addStat(Stats.ITEM_USED.get(this));
        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        return ActionResult.resultConsume(itemstack);
    }
    @OnlyIn(Dist.CLIENT)
    public static EntityModel<Entity> getModelByPlushType(MobPlushy plushItem){
        switch(plushItem.plushType){
            case ZOMBIE:
            return new ZombiePlushyModel();
            case SKELETON:
            return new SkeletonPlushyModel();
            case CREEPER:
            return new CreeperPlushyModel();
        }
        return null;
    }
    public static ResourceLocation getPlushTexture(MobPlushy plushItem){
        switch(plushItem.plushType){
            case SKELETON:            
            return UpgradedWolves.getId("textures/entity/skeleton_plush.png");
            case ZOMBIE:         
            return UpgradedWolves.getId("textures/entity/zombie_plush.png");
            case CREEPER:        
            return UpgradedWolves.getId("textures/entity/creeper_plush.png");
        }
        return null;
    }
}
