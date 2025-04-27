package com.brothers_trouble.postit.item;

import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.EntityRegistry;
import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
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
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class PostItItem extends Item{

//    public static final String COLOR_TAG = "Color";
//    private static DyedItemColor itemColor = new DyedItemColor(-1, true);
    public static boolean used = false;


    public PostItItem(Properties properties) {
        super(properties
                .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFFFFFFFF, true))
//                .component(DataComponents.BANNER_PATTERNS, new BannerPatternLayers(null))

        );
    }

    public static void setDyeColor(ItemStack stack, DyeItem dyeItem) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeItem.getDyeColor().getTextColor(), false));
    }

    public static void setDyeColor(ItemStack stack, DyeColor dyeColor) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextColor(), false));
    }

    // Method to get the dye color
    public static int getDyeColor(ItemStack stack) {
        return DyedItemColor.getOrDefault(stack, Color.GREEN.getRGB());
//        return stack.get(DataComponents.DYED_COLOR);
    }

    public static ItemStack getItemWithColor(ItemStack stack, DyeItem dye){
        setDyeColor(stack, dye);
        return stack;
    }


    @Override
    //create the method for when the item is being used on a block, taking in the context of UseOnContext
    public InteractionResult useOn(UseOnContext context) {
        Direction facing = vec3ToHorizontalDirection(context.getPlayer().getViewVector(1));
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        System.out.println(itemstack.toString());
//        int currentColor = getDyeColor(itemstack);
        if(context.getPlayer().isShiftKeyDown()){
            Vec3 vec3 = context.getClickLocation();
            Direction side = context.getClickedFace();
//            PostItMenu postItMenu = new PostItMenu(level, player);
            PostItEntity postItEntity = new PostItEntity(EntityRegistry.POST_IT_NOTE_ENTITY.get(), level, side, facing, itemstack, player);
//            System.out.println(level.isClientSide() + "<client side? : block face>" + side);
            postItEntity.setPos(vec3);


            level.addFreshEntity(postItEntity);
            context.getItemInHand().shrink(1);
            System.out.println(getDyeColor(itemstack));
            setDyeColor(itemstack, DyeItem.byColor(DyeColor.CYAN));
            System.out.println(getDyeColor(itemstack));

//            setDyeColor(itemstack, 1);
//            System.out.println(currentColor + " is the current color");
//            switch (currentColor){
//                case -1:
//                    setDyeColor(itemstack, 1);
//                case 1:
//                    setDyeColor(itemstack, 2);
//                case 2:
//                    setDyeColor(itemstack, 3);
//                case 3:
//                    setDyeColor(itemstack, 4);
//                case 4:
//                    setDyeColor(itemstack, 5);
//                case 5:
//                    setDyeColor(itemstack, 6);
//                case 6:
//                    setDyeColor(itemstack, 7);
//                case 7:
//                    setDyeColor(itemstack, 8);
//                case 8:
//                    setDyeColor(itemstack, 9);
//                case 9:
//                    setDyeColor(itemstack, 10);
//                case 10:
//                    setDyeColor(itemstack, 11);
//                case 11:
//                    setDyeColor(itemstack, 12);
//                case 12:
//                    setDyeColor(itemstack, 13);
//                case 13:
//                    setDyeColor(itemstack, 14);
//                case 14:
//                    setDyeColor(itemstack, 15);
//                default:
//                    System.out.println(currentColor + " before");
//                    setDyeColor(itemstack, 3);
//            }
////            setDyeColor(itemstack, 14);
//            currentColor = getDyeColor(itemstack);
//            System.out.println(currentColor + " after");

            used = true;
            return InteractionResult.SUCCESS;
        }
//        setDyeColor(itemstack, DyeColor.RED);
        return InteractionResult.FAIL;
    }

    public static Direction vec3ToHorizontalDirection(Vec3 vec) {
        return Direction.fromYRot(Math.atan2(vec.z, vec.x) * (180F / (float)Math.PI));
    }

    public boolean isUsed(){
        return this.used;
    }


//    public EntityType<?> getType(ItemStack stack) {
//        CustomData customdata = (CustomData)stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
//        return !customdata.isEmpty() ? (EntityType)customdata.read(ENTITY_TYPE_FIELD_CODEC).result().orElse(this.getDefaultType()) : this.getDefaultType();
//    }
}
