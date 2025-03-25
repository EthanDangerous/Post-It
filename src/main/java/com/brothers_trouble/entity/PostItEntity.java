package com.brothers_trouble.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public class PostItEntity extends Entity {
    public static final EntityDataAccessor<Integer> DATA_SIDE = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.INT);
//    public static final EntityDataAccessor<Direction> DATA_DIRECTION = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.DIRECTION);
//    private EntityDimensions dimensions = EntityDimensions.fixed(0.25F, 0.25F);
    protected Direction direction;
    private BlockPos pos;
    private PartPose entityPos = PartPose.ZERO;
    private Level level;

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level, Direction face, BlockPos pos) {
        super(entityType, level);
        this.pos = pos;
        this.level = level;
        if(face != null){
            this.getEntityData().set(DATA_SIDE, face.get3DDataValue());
        }
//        this.setDirection(face);
        makeBoundingBox();
//        refreshDimensions();
        System.out.println("EntityDataAccessor: " + this.getEntityData().get(DATA_SIDE));
    }

    public InteractionResult interact(Player player, InteractionHand hand){
        System.out.println("Interaction");
        return InteractionResult.SUCCESS;
    }

//    protected void setDirection(Direction facingDirection) {
//        Validate.notNull(facingDirection);
//        this.direction = facingDirection;
//        if (facingDirection.getAxis().isHorizontal()) {
//            this.setXRot(0.0F);
//            this.setYRot((float)(this.direction.get2DDataValue() * 90));
//        } else {
//            this.setXRot((float)(-90 * facingDirection.getAxisDirection().getStep()));
//            this.setYRot(0.0F);
//        }
//
//        this.xRotO = this.getXRot();
//        this.yRotO = this.getYRot();
//        this.recalculateBoundingBox();
//    }
//
//    protected final void recalculateBoundingBox() {
//        if (this.direction != null) {
//            AABB aabb = this.calculateBoundingBox(this.pos, this.direction);
////            Vec3 vec3 = aabb.getCenter();
////            this.setPosRaw(vec3.x, vec3.y, vec3.z);
//            System.out.println("Is this the client side?\n" + level.isClientSide() + "\nAABB input before setting:\n -----------------------------------" + aabb);
////            this.setBoundingBox(aabb);
//            System.out.println("Returned AABB:\n -----------------------------------" + this.getBoundingBox());
//        }
//    }

//    public void tick(){
//        recalculateBoundingBox();
//    }

//    @Override
//    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
//        if (DATA_SIDE.equals(key)) {
//            System.out.println("before " + this.getBoundingBox());
//            this.recalculateBoundingBox();
//            System.out.println("after " + this.getBoundingBox());
//        }
//        super.onSyncedDataUpdated(key);
//    }

//    protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
//        float f = 0.46875F;
//        Vec3 vec3 = new Vec3(entityPos.x, entityPos.y, entityPos.z);
////        EntityPositionSource.getPosition(level);
//        Direction.Axis direction$axis = direction.getAxis();
//        double d0 = direction$axis == Direction.Axis.X ? 0.25 : 0.2;
//        double d1 = direction$axis == Direction.Axis.Y ? 0.25 : 0.2;
//        double d2 = direction$axis == Direction.Axis.Z ? 0.25 : 0.2;
//        return AABB.ofSize(vec3, d0, d1, d2);
//    }

    @Override
    protected AABB makeBoundingBox() {
        float thickness = 0.05f;
        float length = 0.25f;
        if(getSide() == Direction.UP){
            return AABB.ofSize(position().add(0, 0.025, 0), length, thickness, length);
        }else if(getSide() == Direction.DOWN){
            return AABB.ofSize(position().subtract(0, 0.025, 0), length, thickness, length);
        }else if(getSide() == Direction.NORTH){
            return AABB.ofSize(position().subtract(0, 0, 0.025), length, length, thickness);
        }else if(getSide() == Direction.EAST){
            return AABB.ofSize(position().subtract(0, 0.0, 0), 0.25f, 0.1f, 0.25f);
        }else if(getSide() == Direction.SOUTH){
            return AABB.ofSize(position().add(0, 0, 0.025), length, length, thickness);
        }else if(getSide() == Direction.WEST){
            return AABB.ofSize(position().subtract(0, 0.0, 0), 0.25f, 0.1f, 0.25f);
        }
        return AABB.ofSize(position(), 1.0f, 1.0f, 1.0f);
    }

    public Direction getSide() {
//        if (this.facing == null) {
//            // Get the facing direction from the synchronized data
//            int facingValue = this.getEntityData().get(DATA_ROTATION);
//            this.facing = Direction.from3DDataValue(facingValue);
//        }
        return Direction.from3DDataValue(this.getEntityData().get(DATA_SIDE));
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

//    public void setFacing(Direction direction) {
////        this.facing = direction;
//        if (direction != null) {
//            this.getEntityData().set(DATA_SIDE, direction.get3DDataValue());
//        }
//    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SIDE, 0);
//        builder.define(DATA_DIRECTION, Direction.UP);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
//        if (compoundTag.contains("Facing")) {
//            this.setFacing(Direction.from3DDataValue(compoundTag.getInt("Facing")));
//        }
        if (compoundTag.contains("Rotation")) {
            this.getEntityData().set(DATA_SIDE, compoundTag.getInt("Rotation"));
        }
//        if (compoundTag.contains("Facing")) {
//            // Correctly read the direction using the DIRECTION serializer
//            Direction savedDirection = compoundTag.get("Facing", Direction.class);
//            if (savedDirection != null) {
//                this.getEntityData().set(DATA_DIRECTION, savedDirection);
//            }
//        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
//        if (this.facing != null) {
//            compoundTag.putInt("Facing", this.facing.get3DDataValue());
//        }
        compoundTag.putInt("Rotation", this.getEntityData().get(DATA_SIDE));
//        compoundTag.putInt("Facing", this.getEntityData().get(DATA_DIRECTION));
    }
}
