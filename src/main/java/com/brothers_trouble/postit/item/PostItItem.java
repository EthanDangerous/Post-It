package com.brothers_trouble.postit.item;

import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.EntityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PostItItem extends Item{
//    private DyedItemColor color;
    private Component color;

    public PostItItem(Properties properties) {
        super(properties);
//        this.color = new Component();
//        this.color = new DyedItemColor();
    }

    public int getColor(ItemStack stack) {
        // Use NBT or the DyedItemColor component to get the color
//        return stack.getOrCreate(DyedItemColor.COMPONENT).getColor();
        return -1;
    }

    public void setColor(ItemStack stack, int color) {
//        stack.set(DyedItemColor.COMPONENT, new DyedItemColor(color));
    }

//    protected boolean mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos pos) {
//        return !player.level().isOutsideBuildHeight(pos) && player.mayUseItemAt(pos, direction, itemStack);
//    }

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

            return InteractionResult.SUCCESS;
        }
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
