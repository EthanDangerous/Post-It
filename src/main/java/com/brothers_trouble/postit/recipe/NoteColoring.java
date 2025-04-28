package com.brothers_trouble.postit.recipe;

import com.brothers_trouble.postit.item.PostItItem;
import com.brothers_trouble.postit.registration.ItemRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.neoforged.neoforge.common.Tags;

//import static com.brothers_trouble.postit.registration.RecipeRegistry.NOTE_COLORING_SERIALIZER;
import static com.brothers_trouble.postit.registration.RecipeRegistry.NOTE_COLORING_SERIALIZER;
import static com.brothers_trouble.postit.registration.RecipeRegistry.NOTE_COLORING_TYPE;

public class NoteColoring extends CustomRecipe {

    public NoteColoring() {
        super(CraftingBookCategory.MISC);
        System.out.println("CONSTRUCTOR FOR PAGE CUTTING RUN");
    }

    public boolean matches(CraftingInput input, Level level) {
        int i = 0;
        int j = 0;

        for(int k = 0; k < input.size(); ++k) {
            ItemStack itemstack = input.getItem(k);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof PostItItem) {
                    ++i;
                } else {
                    if (!itemstack.is(Tags.Items.DYES)) {
                        return false;
                    }

                    ++j;
                }

                if (j > 1 || i > 1) {
                    return false;
                }
            }
        }

        return i == 1 && j == 1;
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack itemstack = ItemStack.EMPTY;
        DyeColor dyecolor = DyeColor.WHITE;

        for(int i = 0; i < input.size(); ++i) {
            ItemStack itemstack1 = input.getItem(i);
            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();
                if (item instanceof PostItItem) {
                    itemstack = itemstack1;
                } else {
                    DyeColor tmp = DyeColor.getColor(itemstack1);
                    if (tmp != null) {
                        dyecolor = tmp;
                    }
                }
            }
        }

        PostItItem.setDyeColor(itemstack, dyecolor);
        return itemstack.copyWithCount(4);
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return NOTE_COLORING_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<NoteColoring> {
        @Override
        public MapCodec<NoteColoring> codec() {
            return MapCodec.unit(NoteColoring::new);
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NoteColoring> streamCodec() {
            return StreamCodec.unit(new NoteColoring());
        }
    }
}