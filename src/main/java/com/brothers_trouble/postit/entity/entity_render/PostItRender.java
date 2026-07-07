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

    // ---- geometry, taken straight from post_it_note.geo.json: cube origin [-2,-2,...] size [4,4,...] ----
    // 4 geo units out of a 16-unit block = 0.25 blocks wide/tall -> half-extent 0.125
    private static final float HALF_WIDTH  = 2F / 16F;
    private static final float HALF_HEIGHT = 2F / 16F;

    // ---- UV: the geo cube only samples a 4x4 pixel region out of a 16x16 texture (uv [0,0], uv_size [4,4]) ----
    // sampling the full 0..1 range (like a normal single-texture quad) stretches the whole 16x16 canvas
    // - mostly blank - onto the note, which is why the art came out tiny. Match the real region instead.
    private static final float UV_MAX = 4F / 16F;

    // ---- sway animation: matches post_it_sway.animation.json (0 -> -5deg -> 0 over a 6 second loop) ----
    private static final float SWAY_PERIOD_TICKS = 120F; // 6 seconds @ 20 tps
    private static final float SWAY_AMPLITUDE    = 5F;   // degrees

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

        // orient onto whichever block face it's stuck to (confirmed-working switch-based math)
        poseStack.mulPose(getNoteRotation(faceDir, horiDir));

        // notes drift slightly off the block surface on save/reload - correct for it, same as before
        snapNoteToBlock(poseStack, faceDir, entity.position());

        // sway, pinned at the top edge (the geo model's bone pivot sits at y=+HALF_HEIGHT, i.e. the top edge,
        // which is exactly why the note dips from the top rather than swinging from its center)
        float age = entity.tickCount + partialTick + phase(entity);
        float swayAngle = SWAY_AMPLITUDE * 0.5F * (Mth.cos((float) (2 * Math.PI * age / SWAY_PERIOD_TICKS)) - 1F);
        poseStack.translate(0, HALF_HEIGHT, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(swayAngle));
        poseStack.translate(0, -HALF_HEIGHT, 0);

        // quad and text both drawn inside this same pose -> both inherit facing + sway identically
        renderQuad(entity, poseStack, bufferSource, packedLight);
        renderText(entity, entity.getOnPos(), entity.text(), poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    /** deterministic per-entity phase offset so multiple notes don't all sway in lockstep */
    private static float phase(PostItEntity entity) {
        return (entity.getId() * 37) % 1000;
    }

    // notes tend to inexplicably "drift" away from the block surface slightly on save and reload, correct for that.
    // could be a raycast to the target block, but this is much cheaper
    public static void snapNoteToBlock(PoseStack poseStack, Direction faceDir, Vec3 pos) {
        var axis = faceDir.getAxis();
        var ord  = axis.choose(pos.x(), pos.y(), pos.z());
        if (ord == 0) return; // no need for translation

        var step  = faceDir.getAxisDirection().getStep();
        var rdm   = Mth.sign(ord) * step; // determines how the coordinate is "rounded"
        var delta = (rdm - 1.) / 2. - rdm * Mth.frac(Math.abs(ord)); // the "rounded" delta value
        poseStack.translate(0, 0, delta + .01); // +0.01 to avoid the note clipping into the block
    }

    private void renderQuad(PostItEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // single-sided cutout: the old cube had ~0 thickness, so front/back faces nearly coincided and
        // z-fought every frame (the "shows twice" flicker). One culled quad has nothing to fight with.
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        int entityColor = entity.color();
        int r = (entityColor >> 16) & 0xFF;
        int g = (entityColor >> 8) & 0xFF;
        int b = entityColor & 0xFF;

        // front face - visible from the side the note actually faces
        vertex(vc, pose, normal, -HALF_WIDTH,  HALF_HEIGHT, r, g, b, 0,      0,      0, 0,  1, packedLight);
        vertex(vc, pose, normal, -HALF_WIDTH, -HALF_HEIGHT, r, g, b, 0,      UV_MAX, 0, 0,  1, packedLight);
        vertex(vc, pose, normal,  HALF_WIDTH, -HALF_HEIGHT, r, g, b, UV_MAX, UV_MAX, 0, 0,  1, packedLight);
        vertex(vc, pose, normal,  HALF_WIDTH,  HALF_HEIGHT, r, g, b, UV_MAX, 0,      0, 0,  1, packedLight);

        // back face - reverse winding so it's only visible from behind, with the U coordinate
        // mirrored so the artwork reads correctly from that side instead of backwards. This is
        // a second explicit quad rather than disabling backface culling, so there's no risk of
        // both faces z-fighting each other.
        vertex(vc, pose, normal,  HALF_WIDTH,  HALF_HEIGHT, r, g, b, 0,      0,      0, 0, -1, packedLight);
        vertex(vc, pose, normal,  HALF_WIDTH, -HALF_HEIGHT, r, g, b, 0,      UV_MAX, 0, 0, -1, packedLight);
        vertex(vc, pose, normal, -HALF_WIDTH, -HALF_HEIGHT, r, g, b, UV_MAX, UV_MAX, 0, 0, -1, packedLight);
        vertex(vc, pose, normal, -HALF_WIDTH,  HALF_HEIGHT, r, g, b, UV_MAX, 0,      0, 0, -1, packedLight);
    }

    private void vertex(VertexConsumer vc, Matrix4f pose, Matrix3f normal,
                        float x, float y, int r, int g, int b, float u, float v,
                        float nx, float ny, float nz, int light) {
        // transform the local-space normal by the pose's normal matrix ourselves - the 4-arg
        // setNormal(Matrix3f, float, float, float) convenience overload isn't available here,
        // and the plain 3-arg setNormal() alone never rotates along with the note, which is why
        // notes were rendering with the wrong (and often near-invisible) lighting once rotated
        // onto anything but the default orientation.
        Vector3f n = normal.transform(new Vector3f(nx, ny, nz));
        vc.addVertex(pose, x, y, 0)
                .setColor(r, g, b, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(n.x(), n.y(), n.z());
    }

    void renderText(PostItEntity entity, BlockPos pos, SignText text, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // local offset/scale only - no extra rotation needed, we're already in the facing+sway frame
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

    // ---- facing rotation: your confirmed-working math, unchanged ----

    public static final Quaternionf[] signRotations = memoizeQuaternionRotations();

    public static Quaternionf getNoteRotation(Direction faceDir, Direction horiDir) {
        // ordinal 0: Direction.DOWN, ordinal 1: Direction.UP, 2-5: horizontal directions.
        // subtract 2 from horizontal dir ordinal to shift index to 0-3, then switch between
        // face DOWN, UP, and horizontal (index shifts +0, +4, and +8 respectively)
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

    // Calculate the actual note rotation. Handles horizontal and non-horizontal directions separately.
    public static Quaternionf calcQuat(Direction face, Direction hori) {
        return face.getAxis().isHorizontal()
                ? switch (face) { /* horizontal rotation */
            case NORTH -> Axis.YP.rotationDegrees(180);
            case EAST  -> Axis.YP.rotationDegrees(90);
            case WEST  -> Axis.YN.rotationDegrees(90);
            default -> new Quaternionf();
        }
                : Axis.XN.rotationDegrees(90 * face.getAxisDirection().getStep()) /* vertical: rotate note up or down */
                .mul(switch (hori) { /* horizontal rotation */
                    case SOUTH -> Axis.ZP.rotationDegrees(180);
                    case WEST  -> Axis.ZP.rotationDegrees(90);
                    case EAST  -> Axis.ZN.rotationDegrees(90);
                    default -> new Quaternionf();
                });
    }
}