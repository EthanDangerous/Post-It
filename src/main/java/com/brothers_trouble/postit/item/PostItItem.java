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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class PostItItem extends Item{
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;

    /**
     * Reference: net.minecraft.world.item.DyeableLeatherItem / any vanilla item that ships with a
     * default data component value (e.g. WritableBookItem's blank pages, sign items' blank SignText).
     * Used here to give a freshly-crafted note a default white color and empty text before it's ever
     * placed, so PostItEntity always has something sensible to read in its constructor.
     */
    public PostItItem(Properties properties) {
        super(properties
                .component(DataComponents.DYED_COLOR, new DyedItemColor(DEFAULT_COLOR, true))
                .component(ItemRegistry.NOTE_TEXT_COMPONENT, new SignText())
        );
    }

    /**
     * Reference: net.minecraft.world.item.HangingSignItem / SignItem#useOn (both override Item#useOn
     * the same way) for the general "shift-right-click a block face to place a hanging entity" pattern,
     * and net.minecraft.world.entity.decoration.HangingEntity#survives() for the overlap-checking idea
     * (vanilla checks this *after* constructing the entity; we check first so nothing is ever spawned
     * and then immediately discarded).
     * Used here to spawn a PostItEntity stuck to the clicked block face, refusing to do so if another
     * note is already occupying that spot.
     */
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

            Vec3 spawnPos = vec3.add(
                    context.getClickedFace().getStepX() * 0.01,
                    context.getClickedFace().getStepY() * 0.01,
                    context.getClickedFace().getStepZ() * 0.01);

            // Don't let two notes overlap: predict the bounding box the new note would end up with
            // and bail out if an existing PostItEntity is already sitting in that space.
            AABB prospectiveBox = PostItEntity.calculateBoundingBox(side, spawnPos);
            boolean overlapsExistingNote = !level.getEntities((net.minecraft.world.entity.Entity) null,
                    prospectiveBox, e -> e instanceof PostItEntity).isEmpty();
            if (overlapsExistingNote) return InteractionResult.FAIL;

            PostItEntity postItEntity = new PostItEntity(EntityRegistry.POST_IT_NOTE_ENTITY.get(), level, side, facing, stack);
            postItEntity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            // the constructor built its bounding box using position (0,0,0); recompute now that
            // the note is actually where it's going to live.
            postItEntity.refreshBoundingBox();
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