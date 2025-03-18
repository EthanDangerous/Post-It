package com.brothers_trouble.entity_render;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.model.PostItModel;
import com.brothers_trouble.entity.PostItEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class PostItRender extends EntityRenderer<PostItEntity> {
    public PostItRender(EntityRendererProvider.Context context) {
        super(context);
    }

    public ResourceLocation getTextureLocation(PostItEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/entity/post_it.png");
    }

    @Override
    public void render(PostItEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Position the model correctly
        // These offsets depend on how you positioned your model in Blockbench
        poseStack.translate(0, 0, 0);

        // Apply entity rotation
        int rotation = entity.getEntityData().get(PostItEntity.DATA_ROTATION);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 90));

        // Render the model
//        PostItModel model = new PostItModel(modelPart); // Replace modelPart with appropriate parameter
//
//        // Render the model
//        model.renderToBuffer(poseStack, vertexConsumer, packedLight,
//                OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);

        poseStack.popPose();

    }
}
