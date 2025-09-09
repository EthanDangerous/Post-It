package com.brothers_trouble.postit.entity.entity_render;

import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.model.*;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.util.Color;

import java.lang.Math;
import java.util.*;

import static net.minecraft.client.renderer.blockentity.SignRenderer.getDarkColor;

@OnlyIn(Dist.CLIENT)
public class PostItRender extends GeoEntityRenderer<PostItEntity> {
    private final Font font;
    public static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);

    public PostItRender(EntityRendererProvider.Context context) {
        super(context, ModelRegistry.POST_IT_NOTE_MODEL);
        this.font = context.getFont();
    }

    
    @Override
    public void actuallyRender(PoseStack poseStack, PostItEntity entity, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, int colour) {
        var faceDir = entity.face();
        var horiDir = entity.hori();
        poseStack.pushPose();
	    poseStack.mulPose(getNoteRotation(faceDir, horiDir));
	    snapNoteToBlock(poseStack, faceDir, entity.position());

	    super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
	    renderSignText(entity, entity.getOnPos(), entity.text(), poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

	// notes tend to inexplicably "drift" away from the block surface slightly on save and reload, correct for that
	// could be a raycast to the target block, but this is much cheaper
	public static void snapNoteToBlock(PoseStack poseStack, Direction faceDir, Vec3 pos) {
		var axis = faceDir.getAxis();
		var ord  = axis.choose(pos.x(), pos.y(), pos.z());
		if (ord == 0) return; // no need for translation

		var step  = faceDir.getAxisDirection().getStep();
		var rdm   = Mth.sign(ord) * step; // determines how the coordinate is "rounded"
		var delta = (rdm-1.)/2. - rdm * Mth.frac(Math.abs(ord)); // the "rounded" delta value (nerd math)
		poseStack.translate(0, 0, delta + .01); // translating by delta + 0.01 to avoid the note clipping into the block
														  // from observation: note is 0.01 away from block when right on it
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
		// Translate and scale the text according to text offset and scale
        translateSignText(poseStack, entity.textScale(), entity.textOffset());
        
        // modify the poseStack based on the current animation state
        GeoModel<PostItEntity> model = getGeoModel();
        GeoBone bone = model.getBone("bone").orElse(null);
        if (bone != null) {
            poseStack.rotateAround(Axis.XP.rotation(bone.getRotX()), 0, -50, 0);
            poseStack.rotateAround(Axis.YP.rotation(bone.getRotY()), 0, -50, 0);
            poseStack.rotateAround(Axis.ZP.rotation(bone.getRotZ()), 0, -50, 0);
        }

        /* Vanilla code, to be updated accordingly for future MC versions */
        int darkColor = getDarkColor(text);
        int lineHeight = entity.maxTextLineHeight();
        int lineOffset = 4 * lineHeight / 2;
        FormattedCharSequence[] messages = text.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (component) -> {
            List<FormattedCharSequence> list = this.font.split(component, entity.maxTextLineWidth());
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

        for (int m = 0; m < 4; ++m) {
            FormattedCharSequence message = messages[m];
            float xOffset = (float) -this.font.width(message) / 2;
            if (renderOutline)
                 this.font.drawInBatch8xOutline(message, xOffset, m * lineHeight - lineOffset, textColor, darkColor, poseStack.last().pose(), buffer, light);
            else this.font.drawInBatch(message, xOffset, m * lineHeight - lineOffset, textColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, light);
        }
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

		Entity entity = minecraft.getCameraEntity();
		return entity != null && entity.distanceToSqr(Vec3.atCenterOf(pos)) < (double)OUTLINE_RENDER_DISTANCE;
    }

	// Memorize all possible quaternion rotations to minimize performance impact when rendering
	// since recalculating them every time for every entity for every frame is wasteful
    public static final Quaternionf[] signRotations = memoizeQuaternionRotations();

    public static Quaternionf getNoteRotation(Direction faceDir, Direction horiDir) {
		// ordinal 0: Direction.DOWN
	    // ordinal 1: Direction.UP
	    // 2-5: horizontal directions
	    // subtract 2 from horizontal dir ordinal to shift index to 0-3
	    // then switch between face DOWN, UP, and horizontal (index shifts +0, +4, and +8 respectively)
        return signRotations[horiDir.ordinal() - 2 + 4*Math.min(faceDir.ordinal(), 2)];
    }

    public static Quaternionf[] memoizeQuaternionRotations() {
        Quaternionf[] res = new Quaternionf[12];
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            res[dir.ordinal()-2] = calcQuat(Direction.DOWN,    dir);
            res[dir.ordinal()+2] = calcQuat(Direction.UP,      dir);
            res[dir.ordinal()+6] = calcQuat(dir.getOpposite(), dir); // faceDir == dir.getOpposite()
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
