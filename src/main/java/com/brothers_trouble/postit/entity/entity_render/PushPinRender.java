package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PushPinEntity;
import com.brothers_trouble.postit.util.Catenary;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class PushPinRender extends EntityRenderer<PushPinEntity> {
    private static final float HALF = 1.0F / 32.0F;
    private static final int SEGMENTS = 24;
    private static final float WIDTH = 0.006F;
    private static final double SLACK = 1.04;

    private final ModelPart pin;

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PostIt.locate("push_pin"), "main");

    public static final ResourceLocation TEXTURE_LOCATION = PostIt.locate("textures/entity/push_pin.png");

    public PushPinRender(EntityRendererProvider.Context context) {
        super(context);
        this.pin = context.bakeLayer(LAYER).getChild("push_pin");
    }

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
        return TEXTURE_LOCATION;
    }

    @Override
    public boolean shouldRender(@NotNull PushPinEntity entity, @NotNull Frustum frustum, double x, double y, double z) {
        if (super.shouldRender(entity, frustum, x, y, z)) {
            return true;
        }

        List<PushPinEntity> targets = entity.getLinkTargets();
        if (targets.isEmpty()) {
            return false;
        }

        AABB combinedBox = entity.getBoundingBox();
        for (PushPinEntity target : targets) {
            combinedBox = combinedBox.minmax(target.getBoundingBox());
        }
        return frustum.isVisible(combinedBox);
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

        for (PushPinEntity target : entity.getLinkTargets()) {
            renderRope(entity, target, partialTick, poseStack, bufferSource, packedLight);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderRope(PushPinEntity a, PushPinEntity b, float partialTick, PoseStack poseStack,
                            MultiBufferSource buffers, int light) {
        Vec3 start = a.getAttachOffset();
        Vec3 end = b.getPosition(partialTick).subtract(a.getPosition(partialTick)).add(b.getAttachOffset());

        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;
        double d = Math.sqrt(dx * dx + dz * dz);
        double dist = Math.sqrt(d * d + dy * dy);
        if (dist < 1e-3) {
            return;
        }

        Catenary catenary = d > 1e-3 ? Catenary.of(d, dy, dist * SLACK) : null;

        double px = d > 1e-3 ? -dz / d : 1;
        double pz = d > 1e-3 ? dx / d : 0;

        VertexConsumer consumer = buffers.getBuffer(RenderType.leash());
        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i <= 2 * SEGMENTS + 1; i++) {
            boolean secondPass = i > SEGMENTS;
            int index = secondPass ? 2 * SEGMENTS + 1 - i : i;
            double t = index / (double) SEGMENTS;

            float x = (float) (start.x + dx * t);
            float z = (float) (start.z + dz * t);
            float y = (float) (start.y + (catenary != null ? catenary.yAt(d * t) : dy * t));

            float ox = secondPass ? 0 : (float) (px * WIDTH);
            float oy = secondPass ? WIDTH : 0;
            float oz = secondPass ? 0 : (float) (pz * WIDTH);

            consumer.addVertex(matrix, x - ox, y - oy, z - oz).setColor(0.93F, 0.93F, 0.93F, 1.0F).setLight(light);
            consumer.addVertex(matrix, x + ox, y + oy, z + oz).setColor(0.93F, 0.93F, 0.93F, 1.0F).setLight(light);
        }
    }
}