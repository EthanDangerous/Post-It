package com.brothers_trouble.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class postItEntity extends HangingEntity {
    private static EntityDataAccessor<ItemStack> DATA_ITEM;
    private static EntityDataAccessor<Integer> DATA_ROTATION;
    public static final int NUM_ROTATIONS = 8;
    private static final float DEPTH = 0.0625F;
    private static final float WIDTH = 0.75F;
    private static final float HEIGHT = 0.75F;
    private float dropChance;
    private boolean fixed;

    public postItEntity(EntityType<? extends ItemFrame> entityType, Level level) {
        super(entityType, level);
        this.dropChance = 1.0F;
    }

    public postItEntity(Level level, BlockPos pos, Direction facingDirection) {
        this(EntityType.ITEM_FRAME, level, pos, facingDirection);
    }

    public postItEntity(EntityType<? extends ItemFrame> entityType, Level level, BlockPos pos, Direction direction) {
        super(entityType, level, pos);
        this.dropChance = 1.0F;
        this.setDirection(direction);
    }


    @Override
    protected AABB calculateBoundingBox(BlockPos blockPos, Direction direction) {
        return null;
    }

    @Override
    public void playPlacementSound() {

    }

    @Override
    public void dropItem(@Nullable Entity entity) {

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }
}
