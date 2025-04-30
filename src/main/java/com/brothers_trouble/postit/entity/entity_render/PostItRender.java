package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.item.PostItItem;
import com.brothers_trouble.postit.model.PostItModel;
import com.brothers_trouble.postit.entity.PostItEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.brothers_trouble.postit.entity.PostItEntity.DATA_HORIZ;

public class PostItRender extends EntityRenderer<PostItEntity> {
    private final PostItModel model;

    public PostItRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PostItModel(context.bakeLayer(PostItModel.LAYER_LOCATION));
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull PostItEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/entity/post_it_note.png");
    }

    @Override
    public void render(PostItEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        Vec3 rotateAround = new Vec3(0, 0.5, 0);
        Vec3 rotateTex = new Vec3(0, 0, 0);
        poseStack.pushPose();
        entity.setXRot(270);

        if(entity.getSide() != null){
            if (entity.getSide().equals(Direction.NORTH)) {
                entity.setXRot(90);
                entity.setYRot(0);
                poseStack.rotateAround(Axis.ZN.rotationDegrees(90), (float)rotateTex.x, (float)rotateTex.y, (float)rotateTex.z);
                poseStack.rotateAround(Axis.XN.rotationDegrees(90), (float)rotateAround.x, (float)(rotateAround.y-0.5), (float)rotateAround.z);
            }
            if (entity.getSide().equals(Direction.SOUTH)) {
                entity.setXRot(0);
                entity.setYRot(90);
                poseStack.rotateAround(Axis.ZN.rotationDegrees(90), (float)rotateTex.x, (float)rotateTex.y, (float)rotateTex.z);
                poseStack.rotateAround(Axis.XP.rotationDegrees(90), (float)rotateAround.x, (float)(rotateAround.y-0.5), (float)rotateAround.z);
            }
            if (entity.getSide().equals(Direction.EAST)) {
                entity.setXRot(180);
                entity.setYRot(90);
                poseStack.rotateAround(Axis.ZN.rotationDegrees(90), (float)rotateAround.x, (float)(rotateAround.y-0.5), (float)rotateAround.z);
            }
            if (entity.getSide().equals(Direction.WEST)) {
                entity.setXRot(0);
                entity.setYRot(90);
                poseStack.rotateAround(Axis.XN.rotationDegrees(180), (float)rotateTex.x, (float)rotateTex.y, (float)rotateTex.z);
                poseStack.rotateAround(Axis.ZP.rotationDegrees(90), (float)rotateAround.x, (float)(rotateAround.y-0.5), (float)rotateAround.z);
            }
            if (entity.getSide().equals(Direction.DOWN)) {
                Direction direction = entity.getEntityData().get(DATA_HORIZ);
                if(direction == Direction.NORTH){
                    poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
                }else if(direction == Direction.EAST){
                    poseStack.rotateAround(Axis.YP.rotationDegrees(90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
                }else if(direction == Direction.SOUTH){
                    poseStack.rotateAround(Axis.YP.rotationDegrees(0), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
                }else if(direction == Direction.WEST){
                    poseStack.rotateAround(Axis.YN.rotationDegrees(90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
                }
                poseStack.translate(0, 0, 0);
                entity.setXRot(90);
                entity.setYRot(90);
                poseStack.rotateAround(Axis.ZP.rotationDegrees(180), (float)rotateAround.x, (float)(rotateAround.y-0.5), (float)rotateAround.z);
                poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
            if(entity.getSide().equals(Direction.UP)){
                Direction direction = entity.getEntityData().get(DATA_HORIZ);
                if(direction == Direction.NORTH){
                    poseStack.rotateAround(Axis.YP.rotationDegrees(0), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
                }else if(direction == Direction.EAST){
                    poseStack.rotateAround(Axis.YN.rotationDegrees(90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
                }else if(direction == Direction.SOUTH){
                    poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
                }else if(direction == Direction.WEST){
                    poseStack.rotateAround(Axis.YP.rotationDegrees(90), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
                }
            }
        }

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer, this.model.renderType(this.getTextureLocation(entity)),false, false);

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, entity.tags.getInt("color"));

        poseStack.popPose();
    }
}
