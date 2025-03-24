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
    protected Direction direction;
    private BlockPos pos;

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level, Direction face, BlockPos pos) {
        super(entityType, level);
        this.pos = pos;
//        this.facing = face;
        if(face != null){
            this.getEntityData().set(DATA_SIDE, face.get3DDataValue());
        }
        System.out.println("entity facing is set to " + face);
        this.setDirection(face);
//        this.makeBoundingBox();
//        this.setBoundingBox(calculateBoundingBox(pos, face));
    }

    protected void setDirection(Direction facingDirection) {
        Validate.notNull(facingDirection);
        this.direction = facingDirection;
        if (facingDirection.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((float)(this.direction.get2DDataValue() * 90));
        } else {
            this.setXRot((float)(-90 * facingDirection.getAxisDirection().getStep()));
            this.setYRot(0.0F);
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
        System.out.println(this.getBoundingBox());
    }

    protected final void recalculateBoundingBox() {
        if (this.direction != null) {
            AABB aabb = this.calculateBoundingBox(this.pos, this.direction);
            Vec3 vec3 = aabb.getCenter();
            this.setPosRaw(vec3.x, vec3.y, vec3.z);
            this.setBoundingBox(aabb);
        }
    }


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
}
