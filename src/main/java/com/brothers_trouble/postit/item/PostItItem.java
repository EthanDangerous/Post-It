package com.brothers_trouble.postit.item;

import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.EntityRegistry;
import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PostItItem extends Item{
    public static int DEFAULT_COLOR = 0xFFFFFFFF;
    public PostItItem(Properties properties) {
        super(properties
                .component(DataComponents.DYED_COLOR, new DyedItemColor(DEFAULT_COLOR, true))
                        .component(ItemRegistry.NOTE_TEXT_COMPONENT, new SignText())
        );
    }

    public static void setDyeColor(ItemStack stack, DyeColor dyeColor) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextColor(), true));
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player == null || !stack.is(ItemRegistry.POST_IT_NOTE)) return super.useOn(context);

        Direction facing = Direction.fromYRot(player.getYRot());
        Level level = context.getLevel();

        if (player.isShiftKeyDown()) {
            Vec3 vec3 = context.getClickLocation();
            Direction side = context.getClickedFace();

            PostItEntity postItEntity = new PostItEntity(EntityRegistry.POST_IT_NOTE_ENTITY.get(), level, side, facing, stack);
            postItEntity.setPos(vec3);
            level.addFreshEntity(postItEntity);

            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
