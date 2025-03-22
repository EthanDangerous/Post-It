package com.brothers_trouble.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

public class PostItEntity extends Entity {
    public static final EntityDataAccessor<Integer> DATA_ROTATION = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.INT);
    private EntityDimensions dimensions = EntityDimensions.fixed(0.25F, 0.25F);
    public Direction facing = Direction.NORTH;

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level, Direction face) {
        super(entityType, level);
        this.facing = face;
        System.out.println("entity facing is set to " + face);
//        if(face == Direction.NORTH){
//            this.rotate(Rotation.CLOCKWISE_90);
//        }
//        dimensions.makeBoundingBox(4, 0.1, 4);
//        this.dimensions.makeBoundingBox(4, 0.1, 4);
        this.makeBoundingBox();
    }

    public Direction getFacing() {
        return this.facing;
    }

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level) {
        super(entityType, level);
//        dimensions.makeBoundingBox(4, 0.1, 4);
//        this.dimensions.makeBoundingBox(4, 0.1, 4);
        this.makeBoundingBox();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

//    public PostItEntity(Level level, BlockPos pos, Direction facingDirection) {
//        super(EntityType.ITEM_FRAME, level);
//        this.setDirection(facingDirection);
//    }
//
//    public PostItEntity(EntityType<Entity> entityEntityType, Level level) {
//
//    }

//    @Override
//    protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
//        Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -0.46875);
//        return AABB.ofSize(vec3, WIDTH, HEIGHT, DEPTH);
//    }
//
//    @Override
//    public void playPlacementSound() {
//        this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0F, 1.0F);
//    }
//
//    @Override
//    public boolean survives() {
//        return this.level().noCollision(this);
//    }
//
//    @Override
//    public boolean hurt(DamageSource source, float amount) {
//        return !this.fixed && super.hurt(source, amount);
//    }
//
//    @Override
//    public void dropItem(@Nullable Entity entity) {
//
//    }
//
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ROTATION, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
//
//    @Override
//    public InteractionResult interact(Player player, InteractionHand hand) {
//        return player.isCrouching() ? InteractionResult.PASS : InteractionResult.FAIL;
//    }
//
//    @Override
//    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
//        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
//    }
//
//    public int getRotation() {
//        return this.getEntityData().get(DATA_ROTATION);
//    }
//
//    public void setRotation(int rotation) {
//        this.getEntityData().set(DATA_ROTATION, rotation % 8);
//    }
//
//    public float getVisualRotationYInDegrees() {
//        return (float) Mth.wrapDegrees(180 + this.direction.get2DDataValue() * 90 + this.getRotation() * 45);
//    }
}
