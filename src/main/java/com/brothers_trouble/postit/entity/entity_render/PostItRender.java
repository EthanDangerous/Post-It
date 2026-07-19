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
import net.minecraft.util.FastColor;
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

    // font is... the font of the text. because duh
    private final Font font;

    public static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    public static final ResourceLocation TEXTURE_LOCATION = PostIt.locate("textures/entity/post_it_note.png");

    // these have to do with the dimensions of the model
    private static final float HALF_WIDTH  = 2F / 16F;
    private static final float HALF_HEIGHT = 2F / 16F;

    // this is how much of the UV is taken up by the texture
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
        // just grabs the texture for the model
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(PostItEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        // the direction of the face of the block that the note is on (i believe)
        Direction faceDir = entity.face();
        // the global direction that the note is facing (aka the direction that you can read the text clearly in)
        Direction horiDir = entity.hori();

        poseStack.pushPose();

        // this rotates the note to the correct orientation
        poseStack.mulPose(getNoteRotation(faceDir, horiDir));

        // this is all the math for the animation
        // age is... the age of the entity (which should be in ticks)
        float age = entity.tickCount + partialTick + phase(entity);
        // this is the calculated angle for the animation, which isnt really too crazy, math wise
        float swayAngle = SWAY_AMPLITUDE * 0.5F * (Mth.cos((float) (2 * Math.PI * age / SWAY_PERIOD_TICKS)) - 1F);
        // the translation using half_height is just to make sure the note itself stays connected to the wall, and not slide downwards
        poseStack.translate(0, HALF_HEIGHT, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(swayAngle));
        poseStack.translate(0, -HALF_HEIGHT, 0);

        // renderQuad is for the plane itself, and renderText, you would never believe this, renders the text
        renderQuad(entity, poseStack, bufferSource, packedLight);
        renderText(entity, entity.getOnPos(), entity.text(), poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    private static float phase(PostItEntity entity) {
        // this just offsets the animation for the notes, so they arent all swinging together
        return (entity.getId() * 37) % 1000;
    }

    private void renderQuad(PostItEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // this here just makes sure i can render on the front and back of the plane \/
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_LOCATION));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // this is pretty old, but this is just the tinting for the entity. i pulled this from somewhere but i forget where
        // maybe i got it from like, sheep code? its been forever

        // altered for ARGB with the help of Ashley :3
        int entityColor = entity.color();
        int a = FastColor.ARGB32.alpha(entityColor);
        int r = FastColor.ARGB32.red(entityColor);
        int g = FastColor.ARGB32.green(entityColor);
        int b = FastColor.ARGB32.blue(entityColor);

        // to save space, a method was made so i could run all of this stuff in less space
        // these are like, the corners of each plane (front and back)
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
        // this somehow helps with the lighting? i forget why i added this but i dont wanna remove it and mess things up
        Vector3f n = normal.transform(new Vector3f(nx, ny, nz));
        // just the distance between the front and back face of the note
        float z = nz * (QUAD_SEPARATION / 2F);
        vc.addVertex(pose, x, y, z)
                .setColor(r, g, b, 255) // thiiiis is where the color is slapped in
                .setUv(u, v) // this just pulls from the uv for the texture
                .setOverlay(OverlayTexture.NO_OVERLAY) // i dont recall what this is for exactly, but i think its for tinting as well
                .setLight(light) // just does some lighting stuffs
                .setNormal(n.x(), n.y(), n.z()); // man, i dont even remember what a normal is atp its just black magic
    }

    void renderText(PostItEntity entity, BlockPos pos, SignText text, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // for the record, this isnt to change the language of the text
        // this moves the text around to fit it onto the note at the correct size and position
        translateSignText(poseStack, entity.textScale(), entity.textOffset());

        // this does shadows for text, which is just taken from vanilla code
        int darkColor = getDarkColor(text);
        // height and offset, which i think corresponds to space between lines and where the text is fixed onto the note
        int lineHeight = entity.maxTextLineHeight();
        int lineOffset = 4 * lineHeight / 2;

        // this is just stolen from vanilla, i dont even wanna know how this works ngl
        FormattedCharSequence[] messages = text.getRenderMessages(
                Minecraft.getInstance().isTextFilteringEnabled(),
                component -> {
                    List<FormattedCharSequence> list = this.font.split(component, entity.maxTextLineWidth());
                    return list.isEmpty() ? FormattedCharSequence.EMPTY : list.getFirst();
                });

        boolean renderOutline = false;
        int textColor = darkColor;
        int light = packedLight;

        // this i think could let you make the text glow, but i have not tested that so im not sure
        if (text.hasGlowingText()) {
            textColor = text.getColor().getTextColor();
            renderOutline = isOutlineVisible(pos, textColor);
            light = 0xf000f0;
        }

        // also from vanilla code, this is the actual text rendering.
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

    // this makes sure the text thats rendered doesnt z-fight with the note its on
    // or at least it is supposed to
    private static void translateSignText(PoseStack poseStack, float textScale, Vec3 offset) {
        poseStack.translate(offset.x, offset.y, offset.z);
        // for some reason minecraft makes text appear quite big, and we dont want that here
        float scale = textScale / 64;
        poseStack.scale(scale, -scale, scale);
    }

    static boolean isOutlineVisible(BlockPos pos, int textColor) {
        // something something black text needs outlines or else it would be invisible
        if (textColor == DyeColor.BLACK.getTextColor()) return true;

        // this has to do with spyglass jank
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localplayer = minecraft.player;
        if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping())
            return true;

        // outline is visible within a certain distance
        Entity cameraEntity = minecraft.getCameraEntity();
        return cameraEntity != null && cameraEntity.distanceToSqr(Vec3.atCenterOf(pos)) < (double) OUTLINE_RENDER_DISTANCE;
    }

    // pizzer had pulled these methods from some vanilla code somewhere
    // im just gonna be happy that it works and leave it alone

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