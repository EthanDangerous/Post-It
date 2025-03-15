package com.brothers_trouble.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
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

    protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -0.46875);
        Direction.Axis direction$axis = direction.getAxis();
        double d0 = direction$axis == Direction.Axis.X ? 0.0625 : 0.75;
        double d1 = direction$axis == Direction.Axis.Y ? 0.0625 : 0.75;
        double d2 = direction$axis == Direction.Axis.Z ? 0.0625 : 0.75;
        return AABB.ofSize(vec3, d0, d1, d2);
    }

    @Override
    public void playPlacementSound() {

    }

    @Override
    public void dropItem(@Nullable Entity entity) {

    }

    public boolean survives() {
        if (this.fixed) {
            return true;
        } else if (!this.level().noCollision(this)) {
            return false;
        } else {
            BlockState blockstate = this.level().getBlockState(this.pos.relative(this.direction.getOpposite()));
            return !blockstate.isSolid() && (!this.direction.getAxis().isHorizontal() || !DiodeBlock.isDiode(blockstate)) ? false : this.level().getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
        }
    }

    public void move(MoverType type, Vec3 pos) {
        if (!this.fixed) {
            super.move(type, pos);
        }
    }

    public void push(double x, double y, double z) {
        if (!this.fixed) {
            super.push(x, y, z);
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!this.fixed) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else if (!source.is(DamageTypeTags.IS_EXPLOSION) && !this.getItem().isEmpty()) {
                if (!this.level().isClientSide) {
                    this.dropItem(source.getEntity(), false);
                    this.gameEvent(GameEvent.BLOCK_CHANGE, source.getEntity());
                    this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
                }

                return true;
            } else {
                return super.hurt(source, amount);
            }
        } else {
            return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.isCreativePlayer() ? false : super.hurt(source, amount);
        }
    }

    public void kill() {
        super.kill();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ROTATION, 0);
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
    }

    public int getRotation() {
        return (Integer)this.getEntityData().get(DATA_ROTATION);
    }

    public void setRotation(int rotation) {
        this.setRotation(rotation, true);
    }

    private void setRotation(int rotation, boolean updateNeighbours) {
        this.getEntityData().set(DATA_ROTATION, rotation % 8);
        if (updateNeighbours && this.pos != null) {
            this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }

    }

    public SoundEvent getRemoveItemSound() {
        return SoundEvents.ITEM_FRAME_REMOVE_ITEM;
    }

    public SoundEvent getBreakSound() {
        return SoundEvents.ITEM_FRAME_BREAK;
    }

//    public void playPlacementSound() {
//        this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
//    }

    public SoundEvent getPlaceSound() {
        return SoundEvents.ITEM_FRAME_PLACE;
    }




}
