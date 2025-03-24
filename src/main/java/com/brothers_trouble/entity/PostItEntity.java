package com.brothers_trouble.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public class PostItEntity extends Entity {
    public static final EntityDataAccessor<Integer> DATA_SIDE = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.INT);
    private EntityDimensions dimensions = EntityDimensions.fixed(0.25F, 0.25F);
//    public Direction direction;

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level, Direction face, BlockPos pos) {
        super(entityType, level);
//        this.facing = face;
        if(face != null){
            this.getEntityData().set(DATA_SIDE, face.get3DDataValue());
        }
        System.out.println("entity facing is set to " + face);
//        this.makeBoundingBox();
        this.setBoundingBox(calculateBoundingBox(pos, face));
//        this.makeBoundingBox();
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



    //    @Override
    protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -0.46875);
        Direction.Axis direction$axis = direction.getAxis();
        double d0 = direction$axis == Direction.Axis.X ? 0.0625 : 0.75;
        double d1 = direction$axis == Direction.Axis.Y ? 0.0625 : 0.75;
        double d2 = direction$axis == Direction.Axis.Z ? 0.0625 : 0.75;
        return AABB.ofSize(vec3, d0, d1, d2);
    }
//
//    public AABB makeBoundingBox(int x, int y, int z){
//        return this.dimensions.makeBoundingBox(new Vec3(x, y, z));
//    }
//
    public void setRotation(){

    }

    public AABB getAABB(){
        return this.getBoundingBox();
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

    public void setFacing(Direction direction) {
//        this.facing = direction;
        if (direction != null) {
            this.getEntityData().set(DATA_SIDE, direction.get3DDataValue());
        }
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
        builder.define(DATA_SIDE, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
//        if (compoundTag.contains("Facing")) {
//            this.setFacing(Direction.from3DDataValue(compoundTag.getInt("Facing")));
//        }
        if (compoundTag.contains("Rotation")) {
            this.getEntityData().set(DATA_SIDE, compoundTag.getInt("Rotation"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
//        if (this.facing != null) {
//            compoundTag.putInt("Facing", this.facing.get3DDataValue());
//        }
        compoundTag.putInt("Rotation", this.getEntityData().get(DATA_SIDE));
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
