package com.brothers_trouble.entity.entity_render;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.model.PostItModel;
import com.brothers_trouble.entity.PostItEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class PostItRender extends EntityRenderer<PostItEntity> {
    private PostItModel model;
    private boolean adjusted = false;
//    private List<AABB> hitboxes = new ArrayList<>();


    public PostItRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PostItModel(context.bakeLayer(PostItModel.LAYER_LOCATION));
//        initializeHitboxes();

    }


//    public void initializeHitboxes(){
//        hitboxes.add(new AABB(-2, -1, -1, 1, 1, 1));
//        hitboxes.add(new AABB(-3, -1, -1, 1, 1, 1));
//        hitboxes.add(new AABB(-4, -1, -1, 1, 1, 1));
//        hitboxes.add(new AABB(-5, -1, -1, 1, 1, 1));
//        hitboxes.add(new AABB(-6, -1, -1, 1, 1, 1));
//        hitboxes.add(new AABB(-7, -1, -1, 1, 1, 1));
//    }

    public ResourceLocation getTextureLocation(PostItEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/entity/post_it_note.png");
    }

    @Override
    public void render(PostItEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
//        AABB thing = entity.getBoundingBox().move(0, -0.01, 0);
        Vec3 rotateAround = new Vec3(0, 0.5, 0);
        poseStack.pushPose();
        entity.setXRot(270);


//        System.out.println("render facing " + entity.getFacing());

        // Position the model correctly
        // These offsets depend on how you positioned your model in Blockbench
        poseStack.translate(0, 0, 0);
//        System.out.println(entity.getBoundingBox());
//        entity.move();
//        entity.moveRelative();
        if(entity.getSide() != null){
            if (entity.getSide().equals(Direction.NORTH)) {
                entity.setXRot(90);
                entity.setYRot(0);
                poseStack.rotateAround(Axis.XN.rotationDegrees(90), (float)rotateAround.x, (float)(rotateAround.y-0.5), (float)rotateAround.z);
            }
            if (entity.getSide().equals(Direction.SOUTH)) {
                entity.setXRot(0);
                entity.setYRot(90);
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
                poseStack.rotateAround(Axis.ZP.rotationDegrees(90), (float)rotateAround.x, (float)(rotateAround.y-0.5), (float)rotateAround.z);
            }
            if (entity.getSide().equals(Direction.DOWN)) {
                poseStack.translate(0, 0, 0);
                entity.setXRot(90);
                entity.setYRot(90);
//                Vec3 pos1 = new Vec3(-1, -1, -1);
//                Vec3 pos2 = new Vec3(0.5, 0.5, 0.5);
//                AABB thing = new AABB(pos1, pos2);
//
//                entity.setBoundingBox(thing);
                poseStack.rotateAround(Axis.ZP.rotationDegrees(180), (float)rotateAround.x, (float)(rotateAround.y-0.5), (float)rotateAround.z);
                poseStack.rotateAround(Axis.YP.rotationDegrees(180), (float)rotateAround.x, (float)rotateAround.y, (float)rotateAround.z);
            }
//            adjusted = true;
        }

//        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
//        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));


        // Apply entity rotation
//        int rotation = entity.getEntityData().get(PostItEntity.DATA_SIDE);
//        poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 90));

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer, this.model.renderType(this.getTextureLocation(entity)),false, false);

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);


        poseStack.popPose();
    }
}
