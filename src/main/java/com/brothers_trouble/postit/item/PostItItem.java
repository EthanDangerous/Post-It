package com.brothers_trouble.postit.item;

import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.EntityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PostItItem extends Item{

//    public static final String COLOR_TAG = "Color";

    public PostItItem(Properties properties) {
        super(properties);
    }

//    // Method to set the dye color
//    public void setDyeColor(ItemStack stack, int color) {
//        stack.set(DataComponents.DYED_COLOR, color);
////        return stack;
//    }

    public void setDyeColor(ItemStack stack, DyeColor color) {
        // Create a DyedItemColor object from the DyeColor
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(0xFFFFFF, false));
    }

    // Method to get the dye color
    public static int getDyeColor(ItemStack stack) {
        return (int) stack.getOrDefault(DataComponents.DYED_COLOR, 0);
    }

    @Override
    //create the method for when the item is being used on a block, taking in the context of UseOnContext
    public InteractionResult useOn(UseOnContext context) {
        Direction facing = vec3ToHorizontalDirection(context.getPlayer().getViewVector(1));
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if(context.getPlayer().isShiftKeyDown()){
            Vec3 vec3 = context.getClickLocation();
            Direction side = context.getClickedFace();
//            PostItMenu postItMenu = new PostItMenu(level, player);
            PostItEntity postItEntity = new PostItEntity(EntityRegistry.POST_IT_NOTE_ENTITY.get(), level, side, facing, itemstack, player);
            System.out.println(level.isClientSide() + "<client side? : block face>" + side);
            postItEntity.setPos(vec3);


            level.addFreshEntity(postItEntity);
            context.getItemInHand().shrink(1);
//            setDyeColor(itemstack, DyeColor.RED);

            return InteractionResult.SUCCESS;
        }
//        setDyeColor(itemstack, DyeColor.RED);
        return InteractionResult.FAIL;
    }

    public static Direction vec3ToHorizontalDirection(Vec3 vec) {
        return Direction.fromYRot(Math.atan2(vec.z, vec.x) * (180F / (float)Math.PI));
    }


//    public EntityType<?> getType(ItemStack stack) {
//        CustomData customdata = (CustomData)stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
//        return !customdata.isEmpty() ? (EntityType)customdata.read(ENTITY_TYPE_FIELD_CODEC).result().orElse(this.getDefaultType()) : this.getDefaultType();
//    }
}
