package com.brothers_trouble.postit.mixin;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ShearInteractionMixin {
//    private static final Logger LOGGER = LoggerFactory.getLogger(PostIt.MODID);
    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void postit$doPaperShearing(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
        if (other.getItem() == Items.SHEARS && action == ClickAction.SECONDARY && stack.getItem() == Items.PAPER) {
            if(!player.level().isClientSide){
                stack.shrink(1);
                other.hurtAndBreak(1, (ServerLevel) player.level(), (ServerPlayer) player, (i) -> {});
                player.addItem(new ItemStack(ItemRegistry.POST_IT_NOTE, 4));
                player.playSound(SoundEvents.SHEEP_SHEAR, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
            }
            cir.setReturnValue(true);
        }
    }
}
