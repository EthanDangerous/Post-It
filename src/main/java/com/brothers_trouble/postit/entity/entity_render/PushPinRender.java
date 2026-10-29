package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PushPinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PushPinRender extends EntityRenderer<PushPinEntity> {
    private static final float HALF = 1.0F / 32.0F;
    private final ModelPart pin;

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PostIt.locate("push_pin"), "main");

    public static final ResourceLocation TEXTURE_LOCATION = PostIt.locate("textures/entity/push_pin.png");

    public PushPinRender(EntityRendererProvider.Context context) {
        super(context);
        this.pin = context.bakeLayer(LAYER).getChild("push_pin");
    }

    // creating a box as the root mesh
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        var root = mesh.getRoot();

        root.addOrReplaceChild(
                "push_pin",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.ZERO
        );

        return LayerDefinition.create(mesh, 2, 2);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PushPinEntity entity) {
        // just grabs the texture for the model
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(PushPinEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        Direction face = entity.face();

        poseStack.pushPose();
        poseStack.translate(face.getStepX() * HALF, face.getStepY() * HALF, face.getStepZ() * HALF);
        pin.render(poseStack,
                bufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(entity))),
                packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}