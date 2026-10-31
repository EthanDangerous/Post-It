package com.brothers_trouble.postit.model;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PageEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PageModel extends EntityModel<PageEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "page_model"), "main");
    private final ModelPart model;

    public PageModel(ModelPart root) {
        this.model = root.getChild("page");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition model = partdefinition.addOrReplaceChild("page",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.0F, -4.0F, -0.0005F, 6.0F, 8.0F, 0.00001F, new CubeDeformation(0.001F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));


        // double check what this does later
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(PageEntity pageEntity, float v, float v1, float v2, float v3, float v4) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        // this should fix the lighting hopefully?
        model.render(poseStack, vertexConsumer, i, i1, i2);
    }
}