package com.example.upgradedwolves.entities.plushy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

// Made with Blockbench 3.9.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings


public class CreeperPlushyModel extends EntityModel<Entity> {
	private final ModelPart root;
	// private final ModelPart head;
	// private final ModelPart rightHindLeg;
	// private final ModelPart leftHindLeg;
	// private final ModelPart rightFrontLeg;
	// private final ModelPart leftFrontLeg;

	public CreeperPlushyModel(ModelPart modelPart) {
		this.root = modelPart;
		// this.head = modelPart.getChild("head");
		// this.leftHindLeg = modelPart.getChild("leg1");
		// this.rightHindLeg = modelPart.getChild("leg2");
		// this.leftFrontLeg = modelPart.getChild("leg3");
		// this.rightFrontLeg = modelPart.getChild("leg4");
		
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -2.5F, 4.0F, 4.0F, 4.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 7.0F, 2.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 17).addBox(-0.0F, 7.0F, 0.5F, 2.0F, 3.0F, 2.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, 7.0F, 0.5F, 2.0F, 3.0F, 2.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));
	
		partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(12, 8).addBox(-2.0F, 7.0F, -3.5F, 2.0F, 3.0F, 2.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(12, 13).addBox(0.0F, 7.0F, -3.5F, 2.0F, 3.0F, 2.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32); 
	}

	@Override
	public void setupAnim(Entity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_,
			float p_102623_) {
		
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int overlay, int color) {
		this.root().render(poseStack, consumer, packedLight, overlay, color);
		
	}

	public ModelPart root() {
		return this.root;
	}
}