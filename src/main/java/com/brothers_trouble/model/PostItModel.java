package com.brothers_trouble.model;
// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.brothers_trouble.PostIt;
import com.brothers_trouble.entity.PostItEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class PostItModel extends EntityModel<PostItEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "post_it_note_model"), "main");
	private final ModelPart model;

	public PostItModel(ModelPart root) {
		this.model = root.getChild("post_it");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition model = partdefinition.addOrReplaceChild("post_it",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-2.0F, 0.1F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.001F)),
				PartPose.offset(0.0F, 0.1F, 0.0F));


		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(PostItEntity postItEntity, float v, float v1, float v2, float v3, float v4) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
		model.render(poseStack, vertexConsumer, 255, i1, i2);
	}
}