package com.brothers_trouble.item;

import com.brothers_trouble.entity.PostItEntity;
import com.brothers_trouble.registration.EntityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class PostItItem extends Item{
    public PostItItem(Properties properties) {
        super(properties);
    }

//    protected boolean mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos pos) {
//        return !player.level().isOutsideBuildHeight(pos) && player.mayUseItemAt(pos, direction, itemStack);
//    }

    @Override
    //create the method for when the item is being used on a block, taking in the context of UseOnContext
    public InteractionResult useOn(UseOnContext context) {
        System.out.println("test");

        //use the context to get the level data
        Level level = context.getLevel();
        Block clickedBlock = level.getBlockState(context.getClickedPos()).getBlock();
//        BlockPos blockpos = context.getClickLocation();

        Vec3 vec3 = context.getClickLocation();
        Direction side = context.getClickedFace();
        PostItEntity postItEntity = new PostItEntity(EntityRegistry.POST_IT_NOTE_ENTITY.get(), level, side);
        postItEntity.setPos(vec3);

        level.addFreshEntity(postItEntity);

        return InteractionResult.SUCCESS;
    }

//    public EntityType<?> getType(ItemStack stack) {
//        CustomData customdata = (CustomData)stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
//        return !customdata.isEmpty() ? (EntityType)customdata.read(ENTITY_TYPE_FIELD_CODEC).result().orElse(this.getDefaultType()) : this.getDefaultType();
//    }
}
