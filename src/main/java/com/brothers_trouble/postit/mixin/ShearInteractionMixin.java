package com.brothers_trouble.postit.mixin;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(PostIt.MODID);
    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void postit$doPaperShearing(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
        LOGGER.debug("MIXIN STARTED THE THING");
        LOGGER.debug(stack.toString());
        LOGGER.debug(slot.toString());
        LOGGER.debug(action.toString());
        LOGGER.debug(player.toString());
        LOGGER.debug("MIXIN DID ITS LOGGING THING");

        if (other.getItem() == Items.SHEARS && action == ClickAction.SECONDARY && stack.getItem() == Items.PAPER) {
//            ItemStack slotItem = stack.getItem();
            stack.shrink(1);
            other.setDamageValue(other.getDamageValue()-1);
            player.addItem(new ItemStack(ItemRegistry.POST_IT_NOTE, 4));
            player.level().playSound(SoundEvents., 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
            cir.setReturnValue(true);
        }
    }
}
