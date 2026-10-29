package com.brothers_trouble.postit.item;

import com.brothers_trouble.postit.entity.PushPinEntity;
import com.brothers_trouble.postit.registration.EntityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PushPinItem extends Item {
    public PushPinItem(Properties properties) {
        super(properties);
    }

    // this is just taken from the post-it item give or take a few small changes
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        Level level = context.getLevel();
        BlockState clickedState = level.getBlockState(context.getClickedPos());
        if (!PushPinEntity.canAttachTo(clickedState)) return InteractionResult.PASS;

        Direction side = context.getClickedFace();
        Vec3 pos = context.getClickLocation();

        AABB box = PushPinEntity.calculateBoundingBox(side, pos);
        if (!level.getEntitiesOfClass(PushPinEntity.class, box).isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            PushPinEntity pin = new PushPinEntity(EntityRegistry.PUSH_PIN_ENTITY.get(), level, side);
            pin.setPos(pos.x, pos.y, pos.z);
            pin.refreshBoundingBox();
            level.addFreshEntity(pin);

            if (!player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}