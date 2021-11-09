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
// Paste this class into your mod and generate all required imports


public class SkeletonPlushyModel extends EntityModel<Entity> {
	private final ModelPart root;
	// private final ModelPart head;
	// private final ModelPart leftArm;
	// private final ModelPart rightArm;
	// private final ModelPart rightLeg;
	// private final ModelPart leftLeg;

	public SkeletonPlushyModel(ModelPart modelPart) {
		this.root = modelPart;
		// this.head = modelPart.getChild("head");
		// this.leftArm = modelPart.getChild("leg1");
		// this.rightArm = modelPart.getChild("leg2");
		// this.rightLeg = modelPart.getChild("leg3");
		// this.leftLeg = modelPart.getChild("leg4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(8, 15).addBox(-1.5F, 5.0F, -1F, 1.0F, 5.0F, 1.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(4, 15).addBox(0.5F, 5.0F, -1F, 1.0F, 5.0F, 1.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(12, 8).addBox(-3.0F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 15).addBox(2.0F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));
	
		partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -2.5F, 4.0F, 4.0F, 4.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 5.0F, 2.0F),PartPose.rotation(1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32); 
	}

	@Override
	public void setupAnim(Entity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_,
			float p_102623_) {
		
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int p_103113_, int p_103114_,
			float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
		this.root().render(poseStack, consumer, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
		
	}

	public ModelPart root() {
		return this.root;
	}
}