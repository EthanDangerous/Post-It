package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
import org.joml.*;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.model.*;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.util.Color;

import java.lang.Math;
import java.util.*;
import java.util.function.BiFunction;

import static net.minecraft.client.renderer.blockentity.SignRenderer.getDarkColor;

@OnlyIn(Dist.CLIENT)
public class PostItRender extends GeoEntityRenderer<PostItEntity> {
    private final Font font;
    public static final int              OUTLINE_RENDER_DISTANCE = Mth   .square(16);
    public static final ResourceLocation TEXTURE_LOCATION        = PostIt.locate("textures/entity/post_it_note.png");

    public PostItRender(EntityRendererProvider.Context context) {
        super(context, ModelRegistry.POST_IT_NOTE_MODEL);
        this.font = context.getFont();
    }

    
    @Override
    public void render(PostItEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        var faceDir = entity.face();
        var horiDir = entity.hori();
        poseStack.pushPose();
//        poseStack.translate(0, 0, -0.012);
        //TODO: this here could be used to ensure the note is closer to the block, and ensuring it is inside of the hitbox completely
        poseStack.mulPose(getSignRotation(faceDir, horiDir));

        poseStack.translate(0, -1F/64F, 0); // bring it closer to block face; TODO: find proper value
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
        renderSignText(entity, entity.getOnPos(), entity.text(), poseStack, bufferSource, packedLight);
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

    void renderSignText(PostItEntity entity, BlockPos pos, SignText text, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Apply the same rotation as the PostIt note
        var faceDir = entity.face();
        var horiDir = entity.hori(); // You might want to use actual horizontal direction

        /*
        //poseStack.mulPose(calculateQuaternionRotation(faceDir, horiDir));
        poseStack.translate(0, -0.32, -0.05); // Adjust the -0.1 value as needed
        //poseStack.scale(0.25f, 0.25f, 0.25f); // TODO: this kills the rotation
         */
        translateSignText(poseStack, entity.textScale(), entity.textOffset(), faceDir, horiDir);
        
        // modify the poseStack based on the current animation state
        GeoModel<PostItEntity> model = getGeoModel();
        GeoBone bone = model.getBone("bone").orElse(null);
        if(bone != null) {
            poseStack.rotateAround(Axis.XP.rotation(bone.getRotX()), 0, -50, 0);
            poseStack.rotateAround(Axis.YP.rotation(bone.getRotY()), 0, -50, 0);
            poseStack.rotateAround(Axis.ZP.rotation(bone.getRotZ()), 0, -50, 0);
//            poseStack.mulPose(Axis.XP.rotation(bone.getRotX()));
//            poseStack.mulPose(Axis.YP.rotation(bone.getRotY()));
//            poseStack.mulPose(Axis.ZP.rotation(bone.getRotZ()));
        }
        
        int darkColor = getDarkColor(text);
        int lineHeight = entity.maxTextLineHeight();
        int lineOffset = 4 * lineHeight / 2;
        FormattedCharSequence[] messages = text.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (componment) -> {
            List<FormattedCharSequence> list = this.font.split(componment, entity.maxTextLineWidth());
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.getFirst();
        });
        boolean renderOutline = false;
        int textColor         = darkColor;
        int light             = packedLight;

        if (text.hasGlowingText()) {
            textColor     = text.getColor().getTextColor();
            renderOutline = isOutlineVisible(pos, textColor);
            light         = 0xf000f0;
        }

        for(int m = 0; m < 4; ++m) {
            FormattedCharSequence message = messages[m];
            float xOffset = (float) -this.font.width(message) / 2;
            if (renderOutline)
                 this.font.drawInBatch8xOutline(message, xOffset, m * lineHeight - lineOffset, textColor, darkColor, poseStack.last().pose(), buffer, light);
            else this.font.drawInBatch(message, xOffset, m * lineHeight - lineOffset, textColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, light);
        }

        poseStack.popPose();
    }

    private static void translateSignText(PoseStack poseStack, float textScale, Vec3 offset, Direction faceDir, Direction horiDir) {
        poseStack.mulPose(getTextRotation(faceDir, horiDir));
        poseStack.translate(offset.x, offset.y, offset.z);
        float scale = textScale / 64;
        poseStack.scale(scale, -scale, scale);
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

    public static final Quaternionf[] signRotations = memoizeQuaternionRotations(PostItRender::calculateQuaternionRotationForSign);
    public static final Quaternionf[] textRotations = memoizeQuaternionRotations(PostItRender::calculateQuaternionRotationForText);

    public static Quaternionf getSignRotation(Direction faceDir, Direction horiDir) {
        return signRotations[horiDir.ordinal() - 2 + 4* Math.min(faceDir.ordinal(), 2)];
    }

    public static Quaternionf getTextRotation(Direction faceDir, Direction horiDir) {
        return textRotations[horiDir.ordinal() - 2 + 4*Math.min(faceDir.ordinal(), 2)];
    }

    public static Quaternionf[] memoizeQuaternionRotations(BiFunction<Direction, Direction, Quaternionf> calcFunc) {
        Quaternionf[] res    = new Quaternionf[12];
        Direction[] dirs     = Direction.values(); Arrays.sort(dirs);
        Direction[] horiDirs = Arrays.copyOfRange(dirs, 2, 6);

        for (Direction dir : horiDirs) {
            res[dir.ordinal()-2] = calcFunc.apply(Direction.DOWN,    dir);
            res[dir.ordinal()+2] = calcFunc.apply(Direction.UP,      dir);
            res[dir.ordinal()+6] = calcFunc.apply(dir.getOpposite(), dir); // faceDir == dir.getOpposite()
        }

        return res;
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
                ? Axis.YP.rotationDegrees(3*face.toYRot()).mul(Axis.ZP.rotationDegrees(-90))
                : Axis.YP.rotationDegrees(180*(face.get3DDataValue() - 1))
                .mul(Axis.XP.rotationDegrees(-hori.toYRot() + 90));
    }

    public static Quaternionf calculateQuaternionRotationForText(Direction face, Direction hori) {
        return face.getAxis().isHorizontal()
                ? Axis.YP.rotationDegrees(-face.toYRot())
                : Axis.XP.rotationDegrees(90*(1 - 2*face.get3DDataValue()))
                .mul(Axis.ZP.rotationDegrees(3*hori.toYRot() + 180));
    }

}
