package com.brothers_trouble.postit.item;

import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.menu.screen.NoteScreen;
import com.brothers_trouble.postit.registration.EntityRegistry;
import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class PostItItem extends Item{
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;

    public PostItItem(Properties properties) {
        super(properties
                .component(DataComponents.DYED_COLOR, new DyedItemColor(DEFAULT_COLOR, true))
                        .component(ItemRegistry.NOTE_TEXT_COMPONENT, new SignText())
        );
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
            if (side.getAxis().isHorizontal()) facing = side.getOpposite(); // snap facing direction to side if horizontal

            PostItEntity postItEntity = new PostItEntity(EntityRegistry.POST_IT_NOTE_ENTITY.get(), level, side, facing, stack);
            postItEntity.setPos(vec3.add(context.getClickedFace().getStepX() * 0.01, context.getClickedFace().getStepY() * 0.01, context.getClickedFace().getStepZ() * 0.01));
            level.addFreshEntity(postItEntity);

            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
//        ItemStack itemstack = player.getItemInHand(hand);
//
//        if (!level.isClientSide) {
//            if (!player.isShiftKeyDown()) {
//                Minecraft.getInstance().setScreen(new NoteScreen(this, false));
//
////                return InteractionResult.SUCCESS;
//            }
//        }
////            player.awardStat(Stats.ITEM_USED.get(this));
////            player.gameEvent(GameEvent.ITEM_INTERACT_START);
//
//        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
//    }
}
