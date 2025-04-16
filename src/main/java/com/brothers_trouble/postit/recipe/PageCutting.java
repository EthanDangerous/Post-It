package com.brothers_trouble.postit.recipe;

import com.brothers_trouble.postit.registration.ItemRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;

import static com.brothers_trouble.postit.registration.RecipeRegistry.PAGE_CUTTING_SERIALIZER;
import static com.brothers_trouble.postit.registration.RecipeRegistry.PAGE_CUTTING_TYPE;

public class PageCutting extends CustomRecipe {



    public PageCutting(){
        super(CraftingBookCategory.MISC);
        System.out.println("CONSTRUCTOR FOR PAGE CUTTING RUN");
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

    public ItemStack getResultItem(RegistryAccess access) {
        return new ItemStack(ItemRegistry.POST_IT_NOTE);
    }

    @Override
    public String getGroup() {
        return "page_cutting";
    }

    @Override
    public RecipeType<?> getType() {
        return PAGE_CUTTING_TYPE.get();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return i == 2 && i1 == 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PAGE_CUTTING_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<PageCutting> {
        @Override
        public MapCodec<PageCutting> codec() {
            return MapCodec.unit(PageCutting::new);
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PageCutting> streamCodec() {
            return StreamCodec.unit(new PageCutting());
        }
    }

//    public static RecipeType
}
