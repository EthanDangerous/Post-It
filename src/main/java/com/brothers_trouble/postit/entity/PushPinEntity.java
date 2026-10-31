package com.brothers_trouble.postit.entity;

import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PushPinEntity extends Entity {
    private static final int SUPPORT_CHECK_INTERVAL = 4;
    private static final double PIN_SIZE = 1.0 / 16.0;
    private static final double MAX_LINK_DISTANCE = 16.0;
    private static final String LINK_IDS_KEY = "Ids";
    private static final Map<UUID, UUID> PENDING_LINKS = new HashMap<>();

    protected static final EntityDataAccessor<Direction> FACE_DIRECTION =
            SynchedEntityData.defineId(PushPinEntity.class, EntityDataSerializers.DIRECTION);

    protected static final EntityDataAccessor<CompoundTag> LINK_IDS =
            SynchedEntityData.defineId(PushPinEntity.class, EntityDataSerializers.COMPOUND_TAG);

    private final Set<UUID> linkUuids = new LinkedHashSet<>();

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
        if (this.entityData == null) {
            return super.makeBoundingBox();
        }
        return calculateBoundingBox(face(), this.position());
    }

    public void refreshBoundingBox() {
        this.setBoundingBox(makeBoundingBox());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide || this.isRemoved()) {
            return;
        }

        if (this.tickCount % SUPPORT_CHECK_INTERVAL == 0 && !this.survives()) {
            this.dropAndDiscard();
            return;
        }

        if (!linkUuids.isEmpty() && this.tickCount % SUPPORT_CHECK_INTERVAL == 0) {
            refreshLinkIds();
        }

        if (!this.level().getEntities(this, this.getBoundingBox().inflate(0.5), e -> e instanceof FallingBlockEntity).isEmpty()) {
            this.dropAndDiscard();
        }
    }

    public boolean survives() {
        if (this.level().isOutsideBuildHeight(this.blockPosition())) {
            return false;
        }

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

    public void setLink(PushPinEntity other) {
        // this adds the other pin to this pin's set of links, then updates what the clients see
        this.linkUuids.add(other.getUUID());
        refreshLinkIds();
    }

    public boolean isLinkedTo(PushPinEntity other) {
        // this is only accurate on the server, which is where it gets used (stops duplicate links)
        return this.linkUuids.contains(other.getUUID());
    }

    public void clearLinks() {
        // this just clears every link on this pin, but right now nothing calls this
        // add some way to remove links later(?)
        this.linkUuids.clear();
        this.entityData.set(LINK_IDS, new CompoundTag());
    }

    private void refreshLinkIds() {
        // this turns the saved UUIDs into entity IDs for whichever linked pins are currently loaded
        // it only sends an update to clients if the list actually changed
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        List<Integer> ids = new ArrayList<>();
        for (UUID uuid : linkUuids) {
            if (serverLevel.getEntity(uuid) instanceof PushPinEntity pin && !pin.isRemoved()) {
                ids.add(pin.getId());
            }
        }

        int[] newIds = ids.stream().mapToInt(Integer::intValue).toArray();
        if (!Arrays.equals(newIds, syncedIds())) {
            CompoundTag tag = new CompoundTag();
            tag.putIntArray(LINK_IDS_KEY, newIds);
            this.entityData.set(LINK_IDS, tag);
        }
    }

    private int[] syncedIds() {
        return this.entityData.get(LINK_IDS).getIntArray(LINK_IDS_KEY);
    }

    public List<PushPinEntity> getLinkTargets() {
        // this gets every pin on the other end of a link, skipping any that arent loaded or have been removed
        int[] ids = syncedIds();
        if (ids.length == 0) {
            return List.of();
        }

        List<PushPinEntity> targets = new ArrayList<>();
        for (int id : ids) {
            if (this.level().getEntity(id) instanceof PushPinEntity pin && !pin.isRemoved()) {
                targets.add(pin);
            }
        }
        return targets;
    }

    public Vec3 getAttachOffset() {
        // this just moves the attachment point out about half a pixel to make it come from the center of a pin
        Direction f = face();
        return new Vec3(f.getStepX(), f.getStepY(), f.getStepZ()).scale(PIN_SIZE / 2);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> dataAccessor) {
        if (dataAccessor == FACE_DIRECTION) {
            refreshBoundingBox();
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.STRING)) {
            if (!this.level().isClientSide){
                handleStringClick(player, held);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (!this.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                player.getInventory().placeItemBackInInventory(getPickupStack());
            }
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            this.discard();
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    private void handleStringClick(Player player, ItemStack held) {
        // this is where the clicks are managed, and in this case it handles both clicks
        UUID playerId = player.getUUID();
        UUID firstId = PENDING_LINKS.remove(playerId);

        // if the player hasnt clicked one already, this is where it would do that
        if (firstId == null) {
            PENDING_LINKS.put(playerId, this.getUUID());
            return;
        }
        // if they click the same pin, do nothing
        if (firstId.equals(this.getUUID())) {
            return;
        }

        // the link is added here if they click a pushpin, the entity (the original pushpin) still exists, and it isnt too far away
        Entity first = ((ServerLevel) this.level()).getEntity(firstId);
        if (first instanceof PushPinEntity a && !a.isRemoved()
                && a.distanceToSqr(this) <= MAX_LINK_DISTANCE * MAX_LINK_DISTANCE) {
            // if these two pins are already connected (in either direction), do nothing and keep the string
            if (a.isLinkedTo(this) || this.isLinkedTo(a)) {
                return;
            }
            a.setLink(this);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
        } else {
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isRemoved()) {
            return false;
        }
        if (this.level().isClientSide) {
            return true;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }

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
        builder.define(LINK_IDS, new CompoundTag());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setFaceDirection(Direction.from3DDataValue(tag.getByte("Face")));

        linkUuids.clear();
        ListTag savedLinks = tag.getList("LinkUUIDs", Tag.TAG_INT_ARRAY);
        for (Tag savedLink : savedLinks) {
            linkUuids.add(NbtUtils.loadUUID(savedLink));
        }
        if (tag.hasUUID("LinkUUID")) {
            linkUuids.add(tag.getUUID("LinkUUID"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putByte("Face", (byte) face().get3DDataValue());

        if (!linkUuids.isEmpty()) {
            ListTag savedLinks = new ListTag();
            for (UUID uuid : linkUuids) {
                savedLinks.add(NbtUtils.createUUID(uuid));
            }
            tag.put("LinkUUIDs", savedLinks);
        }
    }
}