package com.example.upgradedwolves.entities.plushy;

import com.example.upgradedwolves.items.MobPlushy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

public class MobPlushyRenderer extends EntityRenderer<MobPlushyEntity> {
    protected EntityModel<Entity> model;
    protected EntityRendererProvider.Context providerContext;

    public MobPlushyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        providerContext = renderManager;
    }

    public static int getPackedOverlay(MobPlushyEntity livingEntityIn, float uIn) {
        return OverlayTexture.pack(OverlayTexture.u(uIn), OverlayTexture.v(false));
     }

    protected float getOverlayProgress(MobPlushyEntity livingEntityIn, float partialTicks) {
        return 0.0F;
     }

    @Override
    public void render(MobPlushyEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
            MultiBufferSource bufferIn, int packedLightIn) {        
        MobPlushy plush = (MobPlushy)entityIn.getItem().getItem();
        model = MobPlushy.getModelByPlushType(plush,providerContext);        
        matrixStackIn.pushPose();
        int i = OverlayTexture.NO_OVERLAY;
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entityIn)));
        model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, i);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public boolean shouldRender(MobPlushyEntity livingEntityIn, Frustum camera, double camX, double camY,
            double camZ) {
        return super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
    }

    @Override
    public ResourceLocation getTextureLocation(MobPlushyEntity entity) {
        MobPlushy plush = (MobPlushy)entity.getItem().getItem();
        return MobPlushy.getPlushTexture(plush);
    }
    
    
}
