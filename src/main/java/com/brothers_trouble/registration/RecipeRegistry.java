package com.brothers_trouble.registration;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.recipe.PageCutting;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeRegistry implements RecipeSerializer{
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, PostIt.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, PostIt.MODID);

    public static final DeferredHolder<CustomRecipe<PageCutting>> PAGE_CUTTING =
            SERIALIZERS.register("page_cutting", PageCutting::new);

    @Override
    public MapCodec codec() {
        return null;
    }

    @Override
    public StreamCodec streamCodec() {
        return null;
    }

    public static void register(IEventBus eventBus) {
        eventBus.register(ModelRegistry.class);
    }
}
