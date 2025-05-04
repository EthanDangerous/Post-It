package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.model.PostItModel;
import com.brothers_trouble.postit.entity.PostItEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class PostItRender extends EntityRenderer<PostItEntity> {
    private final PostItModel model;
    public static final ResourceLocation TEXTURE_LOCATION = PostIt.locate("textures/entity/post_it_note.png");

    public PostItRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PostItModel(context.bakeLayer(PostItModel.LAYER_LOCATION));
    }

    @Override
    public void render(PostItEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        var faceDir = entity.face();
        var horiDir = entity.face();

        poseStack.mulPose(calculateQuaternionRotation(faceDir, horiDir));

        VertexConsumer modelCons = bufferSource.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, modelCons, packedLight, OverlayTexture.NO_OVERLAY, entity.color());

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PostItEntity entity) {
        return TEXTURE_LOCATION;
    }

    public static Quaternionf calculateQuaternionRotation(Direction face, Direction hori) {
        if (face.getAxis().isHorizontal()) {
            float rot = face.toYRot();
            return Axis.YP.rotationDegrees(90+rot)
                    .mul(Axis.ZP.rotationDegrees(90+2*rot))
                    .mul(Axis.YP.rotationDegrees(180+2*rot));
        } else
            return Axis.ZP.rotationDegrees(180*(face.get3DDataValue()-1))
                    .mul(Axis.YP.rotationDegrees(90-hori.toYRot()));
    }
}
