package com.brothers_trouble.postit.item;

import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.EntityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class PostItItem extends Item{
    public static boolean used = false;



    public PostItItem(Properties properties) {
        super(properties
                .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFFFFFFFF, true))
//                .component(DataComponents.BANNER_PATTERNS, new BannerPatternLayers(null))
        );
    }

    public static void setDyeColor(ItemStack stack, DyeItem dyeItem) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeItem.getDyeColor().getTextColor(), true));
    }

    public static void setDyeColor(ItemStack stack, DyeColor dyeColor) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextColor(), true));
    }

    public static int getDyeColor(ItemStack stack) {
        return DyedItemColor.getOrDefault(stack, Color.WHITE.getRGB());
    }

    public static ItemStack getItemWithColor(ItemStack stack, DyeItem dye){
        setDyeColor(stack, dye);
        return stack;
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction facing = vec3ToHorizontalDirection(context.getPlayer().getViewVector(1));
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if(context.getPlayer().isShiftKeyDown()){
            Vec3 vec3 = context.getClickLocation();
            Direction side = context.getClickedFace();
            PostItEntity postItEntity = new PostItEntity(EntityRegistry.POST_IT_NOTE_ENTITY.get(), level, side, facing, itemstack, player);
            postItEntity.setPos(vec3);


            level.addFreshEntity(postItEntity);
            context.getItemInHand().shrink(1);

            used = true;
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public static Direction vec3ToHorizontalDirection(Vec3 vec) {
        return Direction.fromYRot(Math.atan2(vec.z, vec.x) * (180F / (float)Math.PI));
    }

    public boolean isUsed(){
        return this.used;
    }
}
