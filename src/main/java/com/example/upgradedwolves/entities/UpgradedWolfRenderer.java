package com.example.upgradedwolves.entities;

import java.util.ArrayList;
import java.util.List;

import com.example.upgradedwolves.UpgradedWolves;
import com.example.upgradedwolves.capabilities.IWolfStats;
import com.example.upgradedwolves.capabilities.WolfStatsHandler;
import com.example.upgradedwolves.init.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.LightLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import com.mojang.math.Axis;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;

public class UpgradedWolfRenderer extends WolfRenderer {    
    protected List<ResourceLocation> showWolfTextures = Util.make(new ArrayList<ResourceLocation>(), (textureList) -> {
        textureList.add(UpgradedWolves.getId("textures/entity/silver_wolf.png"));
        textureList.add(UpgradedWolves.getId("textures/entity/dark_wolf.png"));
        textureList.add(UpgradedWolves.getId("textures/entity/spotted_wolf.png"));
    });    

    public UpgradedWolfRenderer(EntityRendererProvider.Context rendererManager) {
        super(rendererManager);
        this.model = new UpgradedWolfModel(rendererManager.bakeLayer(ModModelLayers.UPGRADED_WOLF));
        
    }

    @Override
    public ResourceLocation getTextureLocation(Wolf wolf){
        if(!wolf.isTame())
            return super.getTextureLocation(wolf);
        else{
            IWolfStats handler = WolfStatsHandler.getHandler(wolf);
            switch(handler.getWolfType()){
                case 0:
                    return super.getTextureLocation(wolf);
                case 1:
                    return UpgradedWolves.getId("textures/entity/fighterwolf.png");
                case 2:
                    return UpgradedWolves.getId("textures/entity/scavengerwolf.png");
                case 3:                    
                    if(wolf.hasCustomName() && wolf.getCustomName().getString().equals("Strobe"))
                        return showWolfTextures.get(wolf.getRandom().nextInt(3));
                    return showWolfTextures.get(handler.getWolfFur());
                    
            }
            return UpgradedWolves.getId("textures/entity/pet_dog.png");
        }
    }

    public IWolfStats getCapability(Wolf wolf){
        return WolfStatsHandler.getHandler(wolf);
    }

    @Override
    public void render(Wolf wolf, float entityYaw, float partialTicks, PoseStack matrixStackIn,
            MultiBufferSource bufferIn, int packedLightIn) {        
        super.render(wolf, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if(!wolf.getMainHandItem().isEmpty()){
            ItemStack itemStack = wolf.getMainHandItem();
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, -wolf.yBodyRotO, -wolf.yBodyRot)));
            matrixStackIn.translate(-1 * 0.0625, 0, 7 * 0.0625);
            matrixStackIn.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, -wolf.yBodyRotO, -wolf.yBodyRot)));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, -wolf.yHeadRotO, -wolf.yHeadRot)));
            matrixStackIn.translate(1 * 0.0625, 8.5 * 0.0625, 5.5 * 0.0625);
            matrixStackIn.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTicks, -wolf.xRotO, -wolf.getXRot())));
            matrixStackIn.translate(-1 * 0.0625, 0, 0);

            matrixStackIn.translate(0, 1.0 * 0.0625, 0);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(75F));
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90F));        
            if(wolf.getMainHandItem().getItem() instanceof SwordItem){
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90F));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(45F));
                matrixStackIn.translate(6 * 0.0625, 6 * .0625, 0);
            }

            Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.NONE,packedLightIn,OverlayTexture.NO_OVERLAY,matrixStackIn,bufferIn,wolf.level(), wolf.getId());
            matrixStackIn.popPose();
        }
        Entity tugRopeHolder = getCapability(wolf).getRopeHolder();
        if(tugRopeHolder != null)
            renderLeash(wolf, partialTicks, matrixStackIn, bufferIn, tugRopeHolder);
    }

    private <E extends Entity> void renderLeash(Wolf p_115462_, float p_115463_, PoseStack p_115464_, MultiBufferSource p_115465_, E p_115466_){
        p_115464_.pushPose();
        Vec3 vec3 = p_115466_.getRopeHoldPosition(p_115463_);
        double d0 = (double)(Mth.lerp(p_115463_, p_115462_.yBodyRot, p_115462_.yBodyRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
        Vec3 vec31 = p_115462_.getLeashOffset();
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp((double)p_115463_, p_115462_.xo, p_115462_.getX()) + d1;
        double d4 = Mth.lerp((double)p_115463_, p_115462_.yo, p_115462_.getY()) + vec31.y;
        double d5 = Mth.lerp((double)p_115463_, p_115462_.zo, p_115462_.getZ()) + d2;
        p_115464_.translate(d1, vec31.y, d2);
        float f = (float)(vec3.x - d3);
        float f1 = (float)(vec3.y - d4);
        float f2 = (float)(vec3.z - d5);
        //float f3 = 0.025F;
        VertexConsumer vertexconsumer = p_115465_.getBuffer(RenderType.leash());
        Matrix4f matrix4f = p_115464_.last().pose();
        float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = BlockPos.containing(p_115462_.getEyePosition(p_115463_));
        BlockPos blockpos1 = BlockPos.containing(p_115466_.getEyePosition(p_115463_));
        int k = p_115462_.level().getBrightness(LightLayer.SKY, blockpos);
        int l = p_115462_.level().getBrightness(LightLayer.SKY, blockpos1);

        for(int i1 = 0; i1 <= 24; ++i1) {
            addVertexPair(vertexconsumer, matrix4f, f, f1, f2, 0, 0, k, l, 0.025F, 0.025F, f5, f6, i1, false);
        }

        for(int j1 = 24; j1 >= 0; --j1) {
            addVertexPair(vertexconsumer, matrix4f, f, f1, f2, 0, 0, k, l, 0.025F, 0.0F, f5, f6, j1, true);
        }

        p_115464_.popPose();
    }
    private static void addVertexPair(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174314_, int p_174315_, int p_174316_, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float f = (float)p_174321_ / 24.0F;
        int i = (int)Mth.lerp(f, (float)p_174313_, (float)p_174314_);
        int j = (int)Mth.lerp(f, (float)p_174315_, (float)p_174316_);
        int k = LightTexture.pack(i, j);
        float f1 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = 0.5F * f1;
        float f3 = 0.4F * f1;
        float f4 = 0.3F * f1;
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        p_174308_.addVertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).setColor(f2, f3, f4, 1.0F).setLight(k);
        p_174308_.addVertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).setColor(f2, f3, f4, 1.0F).setLight(k);
     }
}