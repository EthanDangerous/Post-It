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

    /**
     * Reference: any vanilla EntityRendererProvider.Context-taking constructor, e.g.
     * ItemFrameRenderer / PaintingRenderer (net.minecraft.client.renderer.entity). Grabs the shared
     * Font instance we need later to draw the note's text.
     */
    public PostItRender(EntityRendererProvider.Context context) {
        super(context);
        this.font = context.getFont();
    }

    /**
     * Reference: EntityRenderer#getTextureLocation javadoc, same as PaintingRenderer/ItemFrameRenderer.
     * Tells the renderer which texture to bind for this entity; we only have one, so it's constant.
     */
    @Override
    public ResourceLocation getTextureLocation(PostItEntity entity) {
        return TEXTURE_LOCATION;
    }

    /**
     * Reference: EntityRenderer#render javadoc + vanilla HangingEntityRenderer/PaintingRenderer for the
     * "orient to a block face" pattern. Main per-frame entry point: rotate to face the block, snap onto
     * its surface, apply the sway animation, then draw the quad and the sign-style text inside the same
     * pose so both inherit the same facing/sway.
     */
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

    /**
     * Simple per-entity offset trick with no direct vanilla equivalent (closest cousin is how
     * vanilla staggers ambient particle/animation timing per-block-entity, e.g. leaves rustling).
     * Deterministic per-entity phase offset so multiple notes don't all sway in lockstep.
     */
    private static float phase(PostItEntity entity) {
        return (entity.getId() * 37) % 1000;
    }

    /**
     * Custom drift-correction fix; no direct vanilla equivalent, closest reference is how
     * BlockEntityRenderers nudge things by fractions of a pixel to avoid z-fighting with the block
     * they're attached to (e.g. SignRenderer, BannerRenderer).
     * Notes tend to inexplicably "drift" away from the block surface slightly on save and reload,
     * correct for that. Could be a raycast to the target block, but this is much cheaper.
     */
    public static void snapNoteToBlock(PoseStack poseStack, Direction faceDir, Vec3 pos) {
        var axis = faceDir.getAxis();
        var ord  = axis.choose(pos.x(), pos.y(), pos.z());
        if (ord == 0) return; // no need for translation

        var step  = faceDir.getAxisDirection().getStep();
        var rdm   = Mth.sign(ord) * step; // determines how the coordinate is "rounded"
        var delta = (rdm - 1.) / 2. - rdm * Mth.frac(Math.abs(ord)); // the "rounded" delta value
        poseStack.translate(0, 0, delta + .01); // +0.01 to avoid the note clipping into the block
    }

    /**
     * Reference: net.minecraft.client.renderer.ItemInHandRenderer / MapRenderer for the pattern of
     * hand-building a quad straight into a VertexConsumer instead of using a baked BakedModel.
     * Used here instead of a baked model because we need per-instance UV cropping and a dyeable tint
     * that a normal item/block model pipeline doesn't give us easily.
     * Draws a single-sided cutout quad twice (front winding + reversed winding for the back) so the
     * note is visible - and shows the art, not just blank - from either side. entityCutoutNoCull is
     * used below so this holds even if the winding math is ever slightly off.
     */
    private void renderQuad(PostItEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_LOCATION));
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

    /**
     * Reference: standard VertexConsumer builder-chain usage seen throughout net.minecraft.client.renderer
     * (e.g. ItemInHandRenderer, particle renderers). One vertex worth of position/color/uv/overlay/light/normal.
     */
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

    /**
     * Reference: net.minecraft.client.renderer.blockentity.SignRenderer#renderSignText - this method
     * is adapted almost directly from vanilla sign text rendering (same glow/outline handling, same
     * per-line loop), just driven by our own line-height/width instead of a SignBlockEntity's.
     */
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

    /**
     * Reference: SignRenderer's text scaling (offset then scale by 1/64) - matches how vanilla shrinks
     * screen-space-sized text down into world space for signs.
     */
    private static void translateSignText(PoseStack poseStack, float textScale, Vec3 offset) {
        poseStack.translate(offset.x, offset.y, offset.z);
        float scale = textScale / 64;
        poseStack.scale(scale, -scale, scale);
    }

    /**
     * Reference: SignRenderer#isOutlineVisible - this is effectively a straight copy. Decides when
     * glowing text needs the dark outline pass for legibility (black text, scoped, or close enough
     * to the camera).
     */
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

    /**
     * Reference: net.minecraft.client.renderer.blockentity.SignRenderer's static `signRotations`
     * lookup table - same "precompute every possible orientation into an array" approach, adapted
     * to a 12-entry (4 horizontal directions x {down, up, horizontal-face}) table instead of signs'.
     */
    public static Quaternionf getNoteRotation(Direction faceDir, Direction horiDir) {
        // ordinal 0: Direction.DOWN, ordinal 1: Direction.UP, 2-5: horizontal directions.
        // subtract 2 from horizontal dir ordinal to shift index to 0-3, then switch between
        // face DOWN, UP, and horizontal (index shifts +0, +4, and +8 respectively)
        return signRotations[horiDir.ordinal() - 2 + 4 * Math.min(faceDir.ordinal(), 2)];
    }

    /** Builds the {@link #signRotations} lookup table once at class-load time. See {@link #getNoteRotation}. */
    public static Quaternionf[] memoizeQuaternionRotations() {
        Quaternionf[] res = new Quaternionf[12];
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            res[dir.ordinal() - 2] = calcQuat(Direction.DOWN,    dir);
            res[dir.ordinal() + 2] = calcQuat(Direction.UP,      dir);
            res[dir.ordinal() + 6] = calcQuat(dir.getOpposite(), dir); // faceDir == dir.getOpposite()
        }
        return res;
    }

    /**
     * Reference: com.mojang.math.Axis rotation helpers as used throughout vanilla renderers (e.g.
     * ItemFrameRenderer's per-direction rotation switch). Calculate the actual note rotation, handling
     * horizontal and non-horizontal facings separately.
     */
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