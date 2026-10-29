package com.brothers_trouble.postit.entity;

import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PushPinEntity extends Entity {
    private static final int SUPPORT_CHECK_INTERVAL = 4;
    private static final double PIN_SIZE = 1.0 / 16.0;

    // this class is pretty much just the post-it entity class altered to fit what pushpins require

    protected static final EntityDataAccessor<Direction> FACE_DIRECTION =
            SynchedEntityData.defineId(PushPinEntity.class, EntityDataSerializers.DIRECTION);

    public PushPinEntity(EntityType<? extends PushPinEntity> entityType, Level level) {
        this(entityType, level, Direction.UP);
    }

    public PushPinEntity(EntityType<? extends PushPinEntity> entityType, Level level, @NotNull Direction faceDirection) {
        super(entityType, level);
        setFaceDirection(faceDirection);
    }

    public static boolean canAttachTo(BlockState state) {
        return state.is(BlockTags.PLANKS)
                || state.is(BlockTags.LOGS)
                || state.is(BlockTags.WOODEN_SLABS);
    }

    public static AABB calculateBoundingBox(Direction face, Vec3 pos) {
        Vec3 center = pos.add(
                face.getStepX() * PIN_SIZE / 2,
                face.getStepY() * PIN_SIZE / 2,
                face.getStepZ() * PIN_SIZE / 2);
        return AABB.ofSize(center, PIN_SIZE, PIN_SIZE, PIN_SIZE);
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        if (this.entityData == null) return super.makeBoundingBox();
        return calculateBoundingBox(face(), this.position());
    }

    public void refreshBoundingBox() {
        this.setBoundingBox(makeBoundingBox());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide || this.isRemoved()) return;

        if (this.tickCount % SUPPORT_CHECK_INTERVAL == 0 && !this.survives()) {
            this.dropAndDiscard();
            return;
        }

        if (!this.level().getEntities(this, this.getBoundingBox().inflate(0.5), e -> e instanceof FallingBlockEntity).isEmpty()) {
            this.dropAndDiscard();
        }
    }

    public boolean survives() {
        if (this.level().isOutsideBuildHeight(this.blockPosition())) return false;

        Direction face = face();
        Vec3 inside = this.position().subtract(
                face.getStepX() * 0.1,
                face.getStepY() * 0.1,
                face.getStepZ() * 0.1);
        BlockPos supportPos = BlockPos.containing(inside);
        BlockState supportState = this.level().getBlockState(supportPos);

        return canAttachTo(supportState)
                && !supportState.getCollisionShape(this.level(), supportPos).isEmpty();
    }

    protected void dropAndDiscard() {
        this.spawnAtLocation(getPickupStack());
        this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);
        this.discard();
    }

    public ItemStack getPickupStack() {
        return new ItemStack(ItemRegistry.PUSH_PIN);
    }

    public void setFaceDirection(@NotNull Direction faceDirection) {
        Objects.requireNonNull(faceDirection);
        this.entityData.set(FACE_DIRECTION, faceDirection);
        refreshBoundingBox();
    }

    public @NotNull Direction face() {
        return Objects.requireNonNull(this.entityData.get(FACE_DIRECTION));
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> dataAccessor) {
        if (dataAccessor == FACE_DIRECTION) refreshBoundingBox();
        super.onSyncedDataUpdated(dataAccessor);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                player.getInventory().placeItemBackInInventory(getPickupStack());
            }
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            this.discard();
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
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

    @Override
    public @NotNull Vec3 getLightProbePosition(float partialTicks) {
        Direction face = face();
        return this.getEyePosition(partialTicks).add(
                face.getStepX() * 0.1,
                face.getStepY() * 0.1,
                face.getStepZ() * 0.1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FACE_DIRECTION, Direction.UP);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setFaceDirection(Direction.from3DDataValue(tag.getByte("Face")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putByte("Face", (byte) face().get3DDataValue());
    }
}