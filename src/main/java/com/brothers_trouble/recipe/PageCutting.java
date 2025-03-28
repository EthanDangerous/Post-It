package com.brothers_trouble.recipe;

import com.brothers_trouble.item.PostItItem;
import com.brothers_trouble.registration.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;

public class PageCutting extends CustomRecipe {
    public PageCutting(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        int p = 0;
        int sh = 0;
        int sl = 0;
        for(int i = 0; i<craftingInput.size(); i++){
            ItemStack item = craftingInput.getItem(i);
            if (!item.is(Items.AIR)) {
                if(item.is(Items.PAPER)){
                    p++;
                }else if(item.is(Items.SHEARS)){
                    sh++;
                }else if(item.is(Items.SLIME_BALL)){
                    sl++;
                }else{
                    return false;
                }
            }
        }
        if(p == 1 && sh == 1 && sl == 1){
            return true;
        }
        return false;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remains = super.getRemainingItems(input);

        for(int i = 0; i<input.size(); i++){
//            ItemStack tool = ItemStack.EMPTY;
            ItemStack item = input.getItem(i);
            if (item.is(Items.SHEARS)) {
                item.setDamageValue(item.getDamageValue()-1);
//                tool = item;
                remains.set(i, item);
                return remains;
            }
        }
        return super.getRemainingItems(input);
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return new ItemStack(ItemRegistry.POST_IT_NOTE);
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return i == 2 && i1 == 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
