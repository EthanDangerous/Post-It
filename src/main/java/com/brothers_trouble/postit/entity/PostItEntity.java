package com.brothers_trouble.postit.entity;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.item.PostItItem;
import com.brothers_trouble.postit.menu.screen.NoteScreen;
import com.brothers_trouble.postit.registration.EntityRegistry;
import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.*;
import software.bernie.geckolib.animatable.instance.*;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.*;

import java.util.Objects;

public class PostItEntity extends Entity implements GeoEntity {
    // these are just some of the funky text defaults that minecraft has (adjusted for my needs :3)
    public static final float TEXT_SCALE = 1F/6F;
    public static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.0F, 0.0006F); // this is the offset i use to stop z-fighting
    public static final int TEXT_LINE_HEIGHT = 10;
    public static final int MAX_TEXT_WIDTH = 90;

    private static final int SUPPORT_CHECK_INTERVAL = 4; // in ticks. item frames check every tick, but i think 2-4 is more than enough

    // this is the face of the block that the note is attached to (particularly for the top and bottom faces)
    protected static final EntityDataAccessor<Direction> FACE_DIRECTION = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.DIRECTION);
    // this is the actual orientation of the note
    protected static final EntityDataAccessor<Direction> HORI_DIRECTION = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.DIRECTION);

    protected static final EntityDataAccessor<Integer> NOTE_COLOR = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<SignText> NOTE_TEXT = SynchedEntityData.defineId(PostItEntity.class, EntityRegistry.NOTE_TEXT_DATA_SERIALIZER);

    protected final ItemStack stack;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level) {
        this(entityType, level, Direction.UP, Direction.NORTH, new ItemStack(ItemRegistry.POST_IT_NOTE));
    }

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level, @NotNull Direction faceDirection, @NotNull Direction horiDirection, @NotNull ItemStack stack) {
        super(entityType, level);
        assert(stack.is(ItemRegistry.POST_IT_NOTE.get()));

        this.stack = stack.copyWithCount(1);

        SignText text = this.stack.getOrDefault(ItemRegistry.NOTE_TEXT_COMPONENT, new SignText());
        int color     = DyedItemColor.getOrDefault(this.stack, PostItItem.DEFAULT_COLOR);

        setText(text);
        setColor(color);
        setHoriDirection(horiDirection);
        setFaceDirection(faceDirection, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FACE_DIRECTION, Direction.UP);
        builder.define(HORI_DIRECTION, Direction.NORTH);
        builder.define(NOTE_COLOR, PostItItem.DEFAULT_COLOR);
        builder.define(NOTE_TEXT, new SignText());
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide
                && !this.isRemoved()
                && this.tickCount % SUPPORT_CHECK_INTERVAL == 0
                && !this.survives()) {
            this.dropAndDiscard();
        }

        if (!this.level().getEntities(this, this.getBoundingBox().inflate(0.5), e -> e instanceof FallingBlockEntity).isEmpty()) {
            this.dropAndDiscard();
        }
    }

    protected BlockPos attachedBlockPos() {
        return BlockPos.containing(this.position()).relative(this.face().getOpposite());
    }

    public boolean survives() {
        if (this.level().isOutsideBuildHeight(this.blockPosition()) || this.isInFluidType()) return false;

        BlockPos supportPos = attachedBlockPos();
        return this.level().getBlockState(supportPos).isFaceSturdy(this.level(), supportPos, this.face());
    }

    protected void dropAndDiscard() {
        this.spawnAtLocation(getPickupStack());
        this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);
        this.discard();
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> dataAccessor) {
        if (dataAccessor == FACE_DIRECTION) rotateToFace(face());

        super.onSyncedDataUpdated(dataAccessor);
    }

    protected void rotateToFace(@NotNull Direction faceDirection) {
        if (faceDirection.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot(faceDirection.get2DDataValue() * 90);
        } else {
            this.setXRot(-90 * faceDirection.getAxisDirection().getStep());
            this.setYRot(0.0F);
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox(faceDirection);
    }

    protected final void recalculateBoundingBox(Direction faceDirection) {
        this.setBoundingBox(calculateBoundingBox(faceDirection, this.position()));
    }

    public static AABB calculateBoundingBox(Direction direction, Vec3 pos) {
        float thickness = 0.035f;
        float length    = 0.25f;

        Vec3i normal = direction.getNormal();
        Vec3  offset = new Vec3(normal.getX(), normal.getY(), normal.getZ()).scale(thickness / 2);

        Direction.Axis axis = direction.getAxis();
        double x = axis == Direction.Axis.X ? thickness : length;
        double y = axis == Direction.Axis.Y ? thickness : length;
        double z = axis == Direction.Axis.Z ? thickness : length;
        return AABB.ofSize(pos.add(offset), x, y, z);
    }

    public void refreshBoundingBox() {
        recalculateBoundingBox(face());
    }

    public void setFaceDirection(@NotNull Direction faceDirection, boolean forceUpdate) {
        Objects.requireNonNull(faceDirection);
        this.entityData.set(FACE_DIRECTION, faceDirection, forceUpdate);
    }

    public void setHoriDirection(@NotNull Direction horiDirection) {
        Objects.requireNonNull(horiDirection);
        this.entityData.set(HORI_DIRECTION, horiDirection);
    }

    public void setText(@NotNull SignText text) {
        Objects.requireNonNull(text);
        this.entityData.set(NOTE_TEXT, text);
    }

    public void setColor(int color) {
        this.entityData.set(NOTE_COLOR, color);
    }

    public @NotNull Direction face() {
        //FACE OF BLOCK
        return Objects.requireNonNull(this.entityData.get(FACE_DIRECTION));
    }

    public @NotNull Direction hori() {
        //DIRECTION OF PLACEMENT
        return Objects.requireNonNull(this.entityData.get(HORI_DIRECTION));
    }

    public @NotNull SignText text() {
        return Objects.requireNonNull(this.entityData.get(NOTE_TEXT));
    }

    public int color() {
        return Objects.requireNonNull(this.entityData.get(NOTE_COLOR));
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        // check what the player is holding before we do the normal shift-click/open-screen stuff
        // this is basically copying what vanilla signs do with DyeItem/GlowInkSacItem/InkSacItem
        // (signs use a SignApplicator interface for this but that only works on SignBlockEntity, so cant reuse it directly here)
        if (heldStack.getItem() instanceof DyeItem dyeItem) {
            return applyDye(player, heldStack, dyeItem.getDyeColor());
        } else if (heldStack.getItem() instanceof GlowInkSacItem) {
            return applyGlow(player, heldStack, true);
        } else if (heldStack.getItem() instanceof InkSacItem) {
            return applyGlow(player, heldStack, false);
        }

        // pizzer's code
        if (player.isShiftKeyDown()) {
            PostIt.LOGGER.info("Interaction 1");
            if (player.level().isClientSide()) return InteractionResult.PASS;

            player.getInventory().placeItemBackInInventory(getPickupStack());
            remove(RemovalReason.DISCARDED);
        } else {
            PostIt.LOGGER.info("Interaction 2");
            if (player.level().isClientSide()) openScreen();
        }
        return InteractionResult.SUCCESS;
    }

    // this recolors the note's text, same as dyeing a sign
    private InteractionResult applyDye(Player player, ItemStack stack, DyeColor color) {
        // only do this server-side, otherwise the client would be settings its own text
        // and it wouldnt match what the server thinks it is
        if (!this.level().isClientSide) {
            SignText current = text();

            // SignText is immutable (weird?) so setColor() doesnt change current, it hands back a whole new SignText
            // this is only bother updating/consuming the dye if the color is actually gonna be different
            // (same deal as vanilla signs, doesnt burn dye if you dye it the same color it already is)
            if (current.getColor() != color) {
                setText(current.setColor(color));
                this.playSound(SoundEvents.DYE_USE, 1.0F, 1.0F);

                // dont eat the dye if theyre in creative, dont wanna annoy people testing stuff
                if (!player.getAbilities().instabuild) stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    // this is for making the text glow (or un-glow with a normal ink sac), same deal as applyDye above
    private InteractionResult applyGlow(Player player, ItemStack stack, boolean glowing) {
        if (!this.level().isClientSide) {
            SignText current = text();

            // again, only actually do anything if the glow state is gonna change
            // (so glow ink sac-ing an already-glowing note doesnt waste the item)
            if (current.hasGlowingText() != glowing) {
                setText(current.setHasGlowingText(glowing));
                this.playSound(glowing ? SoundEvents.GLOW_INK_SAC_USE : SoundEvents.INK_SAC_USE, 1.0F, 1.0F);

                if (!player.getAbilities().instabuild) stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public ItemStack getPickupStack() {
        ItemStack stack = this.stack;

        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color(), true));
        stack.set(ItemRegistry.NOTE_TEXT_COMPONENT, text());

        return stack;
    }

    protected void openScreen() {
        Minecraft.getInstance().setScreen(new NoteScreen(this, false));
    }

    public int maxTextLineHeight() {
        return TEXT_LINE_HEIGHT;
    }

    public int maxTextLineWidth() {
        return MAX_TEXT_WIDTH;
    }

    public Vec3 textOffset() {
        return TEXT_OFFSET;
    }

    public float textScale() {
        return TEXT_SCALE;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isRemoved()) return false;
        if (this.level().isClientSide) return true;
        if (this.isInvulnerableTo(source)) return false;

        this.markHurt();

        Entity attacker = source.getEntity();
        boolean creative = attacker instanceof Player player && player.getAbilities().instabuild;

        if (creative) {
            this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);
            this.discard();
        } else {
            this.dropAndDiscard();
        }
        return true;
    }

    // copied from mojank serialization slop, don't blame me
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("NoteColor")) setColor(tag.getInt("NoteColor"));

        if (tag.contains("NoteText"))
            SignText.DIRECT_CODEC
                    .parse(NbtOps.INSTANCE, tag.getCompound("NoteText"))
                    .resultOrPartial(PostIt.LOGGER::error)
                    .ifPresent(this::setText);

        if (tag.contains("HorizontalDirection"))
            setHoriDirection(Direction.from2DDataValue(tag.getByte("HorizontalDirection")));

        if (tag.contains("FacingDirection"))
            setFaceDirection(Direction.from3DDataValue(tag.getByte("FacingDirection")), true);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

        tag.putInt("NoteColor", color());

        SignText.DIRECT_CODEC
                .encodeStart(NbtOps.INSTANCE, text())
                .resultOrPartial(PostIt.LOGGER::error)
                .ifPresent(t -> tag.put("NoteText", t));

        tag.putByte("HorizontalDirection", (byte) hori().get2DDataValue());
        tag.putByte("FacingDirection",     (byte) face().get3DDataValue());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Main", 10, this::controller));
    }

    protected static final RawAnimation TEST_ANIM = RawAnimation.begin().thenLoop("animation.post_it.test");

    protected <E extends PostItEntity> PlayState controller(final AnimationState<E> event) {
        return event.setAndContinue(TEST_ANIM);
    }

    @Override
    public Vec3 getLightProbePosition(float partialTicks) { // this just fixes the lighting issue on the top of blocks by making the game think the entity is a bit further from the block for lighting
        return this.getEyePosition(partialTicks).add((new Vec3(this.entityData.get(FACE_DIRECTION).step())).scale(0.1));
    }
}