package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.model.PostItModel;
import com.brothers_trouble.postit.entity.PostItEntity;
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
import org.joml.Quaternionf;

import java.util.List;

import static net.minecraft.client.renderer.blockentity.SignRenderer.getDarkColor;

@OnlyIn(Dist.CLIENT)
public class PostItRender extends EntityRenderer<PostItEntity> {
    private final PostItModel model;
    public static final ResourceLocation TEXTURE_LOCATION = PostIt.locate("textures/entity/post_it_note.png");
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.3333333432674408, 0.046666666865348816);
    private final Font font;
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);

    public PostItRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PostItModel(context.bakeLayer(PostItModel.LAYER_LOCATION));
        this.font = context.getFont();
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

        renderSignText(entity.getOnPos(), entity.text(), poseStack, bufferSource, packedLight, 10, entity.getMaxTextLineWidth(), true);
    }

    void renderSignText(BlockPos pos, SignText text, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth, boolean isFrontText) {
        PostIt.LOGGER.debug("TRYING TO RENDER THE TEXT ON MODEL");
        poseStack.pushPose();
        this.translateSignText(poseStack, isFrontText, this.getTextOffset());
        int i = getDarkColor(text);
        int j = 4 * lineHeight / 2;
        FormattedCharSequence[] aformattedcharsequence = text.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (p_277227_) -> {
            List<FormattedCharSequence> list = this.font.split(p_277227_, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)list.get(0);
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
