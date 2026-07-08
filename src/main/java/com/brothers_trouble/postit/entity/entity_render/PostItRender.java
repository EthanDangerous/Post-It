package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PostItEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

import static net.minecraft.client.renderer.blockentity.SignRenderer.getDarkColor;

@OnlyIn(Dist.CLIENT)
public class PostItRender extends EntityRenderer<PostItEntity> {

    private final Font font;

    public static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    public static final ResourceLocation TEXTURE_LOCATION = PostIt.locate("textures/entity/post_it_note.png");

    private static final float HALF_WIDTH  = 2F / 16F;
    private static final float HALF_HEIGHT = 2F / 16F;

    private static final float UV_MAX = 4F / 16F;

    // sway animation
    private static final float SWAY_PERIOD_TICKS = 120F; // 6 seconds (20 tps)
    private static final float SWAY_AMPLITUDE    = 5F;   // degrees

    // this is the distance between the front and back planes
    private static final float QUAD_SEPARATION = 0.0006F;

    public PostItRender(EntityRendererProvider.Context context) {
        super(context);
        this.font = context.getFont();
    }

    @Override
    public ResourceLocation getTextureLocation(PostItEntity entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(PostItEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        Direction faceDir = entity.face();
        Direction horiDir = entity.hori();

        poseStack.pushPose();

        poseStack.mulPose(getNoteRotation(faceDir, horiDir));

        snapNoteToBlock(poseStack, faceDir, entity.position());

        float age = entity.tickCount + partialTick + phase(entity);
        float swayAngle = SWAY_AMPLITUDE * 0.5F * (Mth.cos((float) (2 * Math.PI * age / SWAY_PERIOD_TICKS)) - 1F);
        poseStack.translate(0, HALF_HEIGHT, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(swayAngle));
        poseStack.translate(0, -HALF_HEIGHT, 0);

        renderQuad(entity, poseStack, bufferSource, packedLight);
        renderText(entity, entity.getOnPos(), entity.text(), poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    private static float phase(PostItEntity entity) {
        return (entity.getId() * 37) % 1000;
    }

    public static void snapNoteToBlock(PoseStack poseStack, Direction faceDir, Vec3 pos) {
        var axis = faceDir.getAxis();
        var ord  = axis.choose(pos.x(), pos.y(), pos.z());
        if (ord == 0) return;

        var step  = faceDir.getAxisDirection().getStep();
        var rdm   = Mth.sign(ord) * step;
        var delta = (rdm - 1.) / 2. - rdm * Mth.frac(Math.abs(ord));
        poseStack.translate(0, 0, delta + .01);
    }

    private void renderQuad(PostItEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_LOCATION));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        int entityColor = entity.color();
        int r = (entityColor >> 16) & 0xFF;
        int g = (entityColor >> 8) & 0xFF;
        int b = entityColor & 0xFF;

        vertex(vc, pose, normal, -HALF_WIDTH,  HALF_HEIGHT, r, g, b, 0,      0,       0, 0,  1, packedLight);
        vertex(vc, pose, normal, -HALF_WIDTH, -HALF_HEIGHT, r, g, b, 0,      UV_MAX,  0, 0,  1, packedLight);
        vertex(vc, pose, normal,  HALF_WIDTH, -HALF_HEIGHT, r, g, b, UV_MAX, UV_MAX,  0, 0,  1, packedLight);
        vertex(vc, pose, normal,  HALF_WIDTH,  HALF_HEIGHT, r, g, b, UV_MAX, 0,       0, 0,  1, packedLight);

        vertex(vc, pose, normal,  HALF_WIDTH,  HALF_HEIGHT, r, g, b, 0,      0,       0, 0, -1, packedLight);
        vertex(vc, pose, normal,  HALF_WIDTH, -HALF_HEIGHT, r, g, b, 0,      UV_MAX,  0, 0, -1, packedLight);
        vertex(vc, pose, normal, -HALF_WIDTH, -HALF_HEIGHT, r, g, b, UV_MAX, UV_MAX,  0, 0, -1, packedLight);
        vertex(vc, pose, normal, -HALF_WIDTH,  HALF_HEIGHT, r, g, b, UV_MAX, 0,       0, 0, -1, packedLight);
    }

    private void vertex(VertexConsumer vc, Matrix4f pose, Matrix3f normal,
                        float x, float y, int r, int g, int b, float u, float v,
                        float nx, float ny, float nz, int light) {
        Vector3f n = normal.transform(new Vector3f(nx, ny, nz));
        float z = nz * (QUAD_SEPARATION / 2F);
        vc.addVertex(pose, x, y, z)
                .setColor(r, g, b, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(n.x(), n.y(), n.z());
    }

    void renderText(PostItEntity entity, BlockPos pos, SignText text, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        translateSignText(poseStack, entity.textScale(), entity.textOffset());

        int darkColor = getDarkColor(text);
        int lineHeight = entity.maxTextLineHeight();
        int lineOffset = 4 * lineHeight / 2;

        FormattedCharSequence[] messages = text.getRenderMessages(
                Minecraft.getInstance().isTextFilteringEnabled(),
                component -> {
                    List<FormattedCharSequence> list = this.font.split(component, entity.maxTextLineWidth());
                    return list.isEmpty() ? FormattedCharSequence.EMPTY : list.getFirst();
                });

        boolean renderOutline = false;
        int textColor = darkColor;
        int light = packedLight;

        if (text.hasGlowingText()) {
            textColor = text.getColor().getTextColor();
            renderOutline = isOutlineVisible(pos, textColor);
            light = 0xf000f0;
        }

        for (int m = 0; m < 4; ++m) {
            FormattedCharSequence message = messages[m];
            float xOffset = (float) -this.font.width(message) / 2;
            if (renderOutline) {
                this.font.drawInBatch8xOutline(message, xOffset, m * lineHeight - lineOffset,
                        textColor, darkColor, poseStack.last().pose(), buffer, light);
            } else {
                this.font.drawInBatch(message, xOffset, m * lineHeight - lineOffset,
                        textColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, light);
            }
        }

        poseStack.popPose();
    }

    private static void translateSignText(PoseStack poseStack, float textScale, Vec3 offset) {
        poseStack.translate(offset.x, offset.y, offset.z);
        float scale = textScale / 64;
        poseStack.scale(scale, -scale, scale);
    }

    static boolean isOutlineVisible(BlockPos pos, int textColor) {
        if (textColor == DyeColor.BLACK.getTextColor()) return true;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localplayer = minecraft.player;
        if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping())
            return true;

        Entity cameraEntity = minecraft.getCameraEntity();
        return cameraEntity != null && cameraEntity.distanceToSqr(Vec3.atCenterOf(pos)) < (double) OUTLINE_RENDER_DISTANCE;
    }

    public static final Quaternionf[] signRotations = memoizeQuaternionRotations();

    public static Quaternionf getNoteRotation(Direction faceDir, Direction horiDir) {
        return signRotations[horiDir.ordinal() - 2 + 4 * Math.min(faceDir.ordinal(), 2)];
    }

    public static Quaternionf[] memoizeQuaternionRotations() {
        Quaternionf[] res = new Quaternionf[12];
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            res[dir.ordinal() - 2] = calcQuat(Direction.DOWN,    dir);
            res[dir.ordinal() + 2] = calcQuat(Direction.UP,      dir);
            res[dir.ordinal() + 6] = calcQuat(dir.getOpposite(), dir); // faceDir == dir.getOpposite()
        }
        return res;
    }

    public static Quaternionf calcQuat(Direction face, Direction hori) {
        if (face.getAxis().isHorizontal()) {
            return switch (face) {
                case NORTH -> Axis.YP.rotationDegrees(180);
                case EAST  -> Axis.YP.rotationDegrees(90);
                case WEST  -> Axis.YN.rotationDegrees(90);
                default    -> new Quaternionf();
            };
        }

        Quaternionf q = Axis.XN.rotationDegrees(90 * face.getAxisDirection().getStep());

        if (face == Direction.DOWN) {
            q.mul(switch (hori) {
                case SOUTH -> Axis.ZP.rotationDegrees(180);
                case WEST  -> Axis.ZN.rotationDegrees(90);
                case EAST  -> Axis.ZP.rotationDegrees(90);
                default    -> new Quaternionf();
            });
        } else {
            q.mul(switch (hori) {
                case SOUTH -> Axis.ZP.rotationDegrees(180);
                case WEST  -> Axis.ZP.rotationDegrees(90);
                case EAST  -> Axis.ZN.rotationDegrees(90);
                default    -> new Quaternionf();
            });
        }

        return q;
    }
}