package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.model.PostItModel;
import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.loading.json.raw.*;
import software.bernie.geckolib.model.*;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.util.Color;

import java.util.*;

import static net.minecraft.client.renderer.blockentity.SignRenderer.getDarkColor;

@OnlyIn(Dist.CLIENT)
public class PostItRender extends GeoEntityRenderer<PostItEntity> {
    
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.3333333432674408, 0.046666666865348816);
    private final Font font;
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);

    public PostItRender(EntityRendererProvider.Context context) {
        super(context, ModelRegistry.POST_IT_NOTE_MODEL);
        this.font = context.getFont();
    }

    
    @Override
    public void render(PostItEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        var faceDir = entity.face();
        var horiDir = entity.hori();
        poseStack.pushPose();
        poseStack.mulPose(calculateQuaternionRotation(faceDir, horiDir));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        
        renderSignText(entity, entity.getOnPos(), entity.text(), poseStack, bufferSource, packedLight, 10, entity.getMaxTextLineWidth(), true);
        poseStack.popPose();
    }

    @Override
    public Color getRenderColor(PostItEntity animatable, float partialTick, int packedLight) {
        Color baseColor = super.getRenderColor(animatable, partialTick, packedLight);

        int entityColor = animatable.color();

        int red = (entityColor >> 16) & 0xFF;
        int green = (entityColor >> 8) & 0xFF;
        int blue = entityColor & 0xFF;

        return Color.ofRGBA(
                (int) (baseColor.getRed() * red / 255.0f),
                (int) (baseColor.getGreen() * green / 255.0f),
                (int) (baseColor.getBlue() * blue / 255.0f),
                baseColor.getAlpha()
        );
    }

    void renderSignText(PostItEntity entity, BlockPos pos, SignText text, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth, boolean isFrontText) {
        poseStack.pushPose();

        // Apply the same rotation as the PostIt note
        var faceDir = entity.face();
        var horiDir = entity.hori(); // You might want to use actual horizontal direction
        //poseStack.mulPose(calculateQuaternionRotation(faceDir, horiDir));
        poseStack.translate(0, -0.08, 0.0); // Adjust the -0.1 value as needed
        //poseStack.scale(0.25f, 0.25f, 0.25f); // TODO: this kills the rotation

        this.translateSignText(poseStack, isFrontText, this.getTextOffset());
        
        // modify the poseStack based on the current animation state
        GeoModel<PostItEntity> model = getGeoModel();
        GeoBone bone = model.getBone("bone").orElse(null);
        if(bone != null) {
            poseStack.mulPose(Axis.XP.rotation(bone.getRotX()));
            poseStack.mulPose(Axis.YP.rotation(bone.getRotY()));
            poseStack.mulPose(Axis.ZP.rotation(bone.getRotZ()));
        }
        
        int i = getDarkColor(text);
        int j = 4 * lineHeight / 2;
        FormattedCharSequence[] aformattedcharsequence = text.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (p_277227_) -> {
            List<FormattedCharSequence> list = this.font.split(p_277227_, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.getFirst();
        });
        int k;
        boolean flag;
        int l;
        if (text.hasGlowingText()) {
            k = text.getColor().getTextColor();
            flag = isOutlineVisible(pos, k);
            l = 15728880;
        } else {
            k = i;
            flag = false;
            l = packedLight;
        }

        for(int i1 = 0; i1 < 4; ++i1) {
            FormattedCharSequence formattedcharsequence = aformattedcharsequence[i1];
            float f = (float)(-this.font.width(formattedcharsequence) / 2);
            if (flag) {
                this.font.drawInBatch8xOutline(formattedcharsequence, f, (float)(i1 * lineHeight - j), k, i, poseStack.last().pose(), buffer, l);
            } else {
                this.font.drawInBatch(formattedcharsequence, f, (float)(i1 * lineHeight - j), k, false, poseStack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, l);
            }
        }

        poseStack.popPose();
    }

    Vec3 getTextOffset() {
        return TEXT_OFFSET;
    }

    static boolean isOutlineVisible(BlockPos pos, int textColor) {
        if (textColor == DyeColor.BLACK.getTextColor()) {
            return true;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localplayer = minecraft.player;
            if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping()) {
                return true;
            } else {
                Entity entity = minecraft.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(pos)) < (double)OUTLINE_RENDER_DISTANCE;
            }
        }
    }

    private void translateSignText(PoseStack poseStack, boolean isFrontText, Vec3 offset) {
        if (!isFrontText) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }

        float f = 0.015625F * this.getSignTextRenderScale();
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.scale(f, -f, f);
    }

    public float getSignTextRenderScale() {
        return 0.6666667F;
    }

//    public static Quaternionf calculateQuaternionRotation(Direction face, Direction hori) {
//        if (face.getAxis().isHorizontal()) {
//            float rot = face.toYRot();
//            return Axis.YP.rotationDegrees(90+rot)
//                    .mul(Axis.ZP.rotationDegrees(90+2*rot))
//                    .mul(Axis.YP.rotationDegrees(180+2*rot));
//        } else{
//            return Axis.ZP.rotationDegrees(180*(face.get3DDataValue()-1))
//                    .mul(Axis.YP.rotationDegrees(90-hori.toYRot()));
//        }
//    }
    public static Quaternionf calculateQuaternionRotation(Direction face, Direction hori) {
        return face.getAxis().isHorizontal()
                ? Axis.YP.rotationDegrees(-face.toYRot())
                : Axis.XP.rotationDegrees(90*(1 - 2*face.get3DDataValue()))
                .mul(Axis.ZP.rotationDegrees(3*hori.toYRot() + 180));
    }

    public static Quaternionf calculateQuaternionRotationForSign(Direction face, Direction hori) {
        // optimized version of the horizontal case:
        //private static final double QUAT_SCALE = Math.sqrt(0.5);
        //final double angle = (face.toYRot()/120-0.25) * Math.PI;
        //final double sin = Math.sin(angle);
        //final double cos = Math.cosFromSin(sin, angle);
        //dest.set((float) (-sin * QUAT_SCALE),
        //		 (float) ( sin * QUAT_SCALE),
        //		 (float) (-cos * QUAT_SCALE),
        //		 (float) ( cos * QUAT_SCALE));

        return face.getAxis().isHorizontal()
                ? Axis.YP.rotationDegrees(3*face.toYRot() - 90).mul(Axis.ZP.rotationDegrees(-90))
                : Axis.ZP.rotationDegrees(180*(face.get3DDataValue() - 1))
                .mul(Axis.YP.rotationDegrees(hori.toYRot() - 90));
    }
}
