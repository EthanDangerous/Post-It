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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public class PostItEntity extends Entity {
    public static final EntityDataAccessor<Integer> DATA_SIDE = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Direction> DATA_HORIZ = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.DIRECTION);
//    private ItemStack item;

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level, Direction face, Direction facing) {
        super(entityType, level);
//        this.item = item;
        if(face != null){
            this.getEntityData().set(DATA_SIDE, face.get3DDataValue());
        }
        if(facing != null){
            this.getEntityData().set(DATA_HORIZ, facing);
        }
        makeBoundingBox();
        System.out.println("EntityDataAccessor: " + this.getEntityData().get(DATA_SIDE));
    }

    public InteractionResult interact(Player player, InteractionHand hand){
        if(player.isShiftKeyDown()){
            System.out.println("Interaction 1");
//            player.addItem(item);
            return InteractionResult.SUCCESS;
        }else{
            System.out.println("Interaction 2");
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected AABB makeBoundingBox() {
        float thickness = 0.05f;
        float length = 0.25f;
        float offset = thickness/2;
        if(getSide() == Direction.UP){
            return AABB.ofSize(position().add(0, offset, 0), length, thickness, length);
        }else if(getSide() == Direction.DOWN){
            return AABB.ofSize(position().subtract(0, offset, 0), length, thickness, length);
        }else if(getSide() == Direction.NORTH){
            return AABB.ofSize(position().subtract(0, 0, offset), length, length, thickness);
        }else if(getSide() == Direction.EAST){
            return AABB.ofSize(position().add(offset, 0, 0), thickness, length, length);
        }else if(getSide() == Direction.SOUTH){
            return AABB.ofSize(position().add(0, 0, offset), length, length, thickness);
        }else if(getSide() == Direction.WEST){
            return AABB.ofSize(position().subtract(offset, 0.0, 0), thickness, length, length);
        }
        return AABB.ofSize(position(), 1.0f, 1.0f, 1.0f);
    }

    public Direction getSide() {
        return Direction.from3DDataValue(this.getEntityData().get(DATA_SIDE));
    }

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level) {
        super(entityType, level);
        this.makeBoundingBox();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SIDE, 0);
        builder.define(DATA_HORIZ, Direction.NORTH);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("Rotation")) {
            this.getEntityData().set(DATA_SIDE, compoundTag.getInt("Rotation"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Rotation", this.getEntityData().get(DATA_SIDE));
    }
}
